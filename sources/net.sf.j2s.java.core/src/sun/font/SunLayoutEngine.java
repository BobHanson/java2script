/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 *
 */

/*
 *
 * (C) Copyright IBM Corp. 2003 - All Rights Reserved
 */

package sun.font;

import sun.font.GlyphLayout.*;
import swingjs.JSFontMetrics;

import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.concurrent.ConcurrentHashMap;

/*
 * different ways to do this
 * 1) each physical font2d keeps a hashtable mapping scripts to layout
 * engines, we query and fill this cache.
 * 2) we keep a mapping independent of font using the key Most likely
 * few fonts will be used, so option 2 seems better
 *
 * Once we know which engine to use for a font, we always know, so we
 * shouldn't have to recheck each time we do layout.  So the cache is
 * ok.
 *
 * Should we reuse engines?  We could instantiate an engine for each
 * font/script pair.  The engine would hold onto the table(s) from the
 * font that it needs.  If we have multiple threads using the same
 * engine, we still need to keep the state separate, so the native
 * engines would still need to be allocated for each call, since they
 * keep their state in themselves.  If they used the passed-in GVData
 * arrays directly (with some checks for space) then since each GVData
 * is different per thread, we could reuse the layout engines.  This
 * still requires a separate layout engine per font, because of the
 * table state in the engine.  If we pushed that out too and passed it
 * in with the native call as well, we'd be ok if the layout engines
 * keep all their process state on the stack, but I don't know if this
 * is true.  Then we'd basically just be down to an engine index which
 * we pass into native and then invoke the engine code (now a
 * procedure call, not an object invocation) based on a switch on the
 * index.  There would be only half a dozen engine objects then, not
 * potentially half a dozen per font.  But we'd have to stack-allocate
 * some state that included the pointer to the required font tables.
 *
 * Seems for now that the way to do things is to come in with a
 * selector and the font.  The selector indicates which engine to use,
 * the engine is stack allocated and initialized with the required
 * font tables (the selector indicates which).  Then layout is called,
 * the contents are copied (or not), and the stack is destroyed on
 * exit. So the association is between the font/script (layout engine
 * desc) and and one of a few permanent engine objects, which are
 * handed the key when they need to process something.  In the native
 * case, the engine holds an index, and just passes it together with
 * the key info down to native.  Some default cases are the 'default
 * layout' case that just runs the c2gmapper, this stays in java and
 * just uses the mapper from the font/strike.  Another default case
 * might be the unicode arabic shaper, since this doesn't care about
 * the font (or script or lang?) it wouldn't need to extract this
 * data.  It could be (yikes) ported back to java even to avoid
 * upcalls to check if the font supports a particular unicode
 * character.
 *
 * I'd expect that the majority of scripts use the default mapper for
 * a particular font.  Loading the hastable with 40 or so keys 30+ of
 * which all map to the same object is unfortunate.  It might be worth
 * instead having a per-font list of 'scripts with non-default
 * engines', e.g. the factory has a hashtable mapping fonts to 'script
 * lists' (the factory has this since the design potentially has other
 * factories, though I admit there's no client for this yet and no
 * public api) and then the script list is queried for the script in
 * question.  it can be preloaded at creation time with all the
 * scripts that don't have default engines-- either a list or a hash
 * table, so a null return from the table means 'default' and not 'i
 * don't know yet'.
 *
 * On the other hand, in most all cases the number of unique
 * script/font combinations will be small, so a flat hashtable should
 * suffice.
 * */
public final class SunLayoutEngine implements LayoutEngine, LayoutEngineFactory {
//    private static native void initGVIDs();
//    static {
//        FontManagerNativeLibrary.load();
//        initGVIDs();
//    }
//
    private LayoutEngineKey key;

    private static LayoutEngineFactory instance;

    public static LayoutEngineFactory instance() {
        if (instance == null) {
            instance = new SunLayoutEngine();
        }
        return instance;
    }

    private SunLayoutEngine() {
        // actually a factory, key is null so layout cannot be called on it
    }

    public LayoutEngine getEngine(Font2D font, int script, int lang) {
        return getEngine(new LayoutEngineKey(font, script, lang));
    }

//  // !!! don't need this unless we have more than one sun layout engine...
    public LayoutEngine getEngine(LayoutEngineKey key) {
        ConcurrentHashMap cache = cacheref;
        if (cache == null) {
            cacheref = cache = new ConcurrentHashMap();
        }

        LayoutEngine e = (LayoutEngine)cache.get(key);
        if (e == null) {
            LayoutEngineKey copy = key.copy();
            e = new SunLayoutEngine(copy);
            cache.put(copy, e);
        }
        return e;
    }
    
    private ConcurrentHashMap cacheref;
//
    private SunLayoutEngine(LayoutEngineKey key) {
        this.key = key;
    }
//
    @Override
	public void layout(FontStrikeDesc desc, float[] mat, int gmask,
                       int baseIndex, TextRecord tr, int typo_flags,
                       Point2D.Float pt, GVData data) {
    
    //SwingJS Note: This is the method in c++ that sets _count, along with _indices, _positions, and _glyphs
    
        
        layout(key.font(), mat, gmask, baseIndex, tr, typo_flags, pt, data);

        
        
//        Font2D font = key.font();
//        FontStrike strike = font.getStrike(desc);
//        long layoutTables = 0;
//        if (font instanceof TrueTypeFont) {
//            layoutTables = ((TrueTypeFont) font).getLayoutTableCache();
//        }
//        nativeLayout(font, strike, mat, gmask, baseIndex,
//             tr.text, tr.start, tr.limit, tr.min, tr.max,
//             key.script(), key.lang(), typo_flags, pt, data,
//             font.getUnitsPerEm(), layoutTables);
    }

	/**
	 * Only used by StandardGlyphVector. 
	 * 
	 * Takes the place of nativeLayout in SunLayoutEngine.
	 * 
	 * @param font2d actually just Font in JavaScript; there is no Font2D
	 * @param mat
	 * @param gmask
	 * @param baseIndex
	 * @param tr
	 * @param typo_flags
	 * @param pt
	 * @param data
	 */
	 static void layout(Font2D font2d, float[] mat, int gmask, int baseIndex, TextRecord tr, int typo_flags,
			Point2D.Float pt, GVData data) {
		Font f = (Font) (Object) font2d;
		FontDesignMetrics fm = (FontDesignMetrics) f.getFontMetrics();
		// TODO: scaling? rotation?
		int g2 = 0;
		float x = 0, y = 0, w; // I suspect all fonts are( laid out linearly in x. Maybe not?
		for (int g = 0, p = tr.start; p < tr.limit; p++, g++) {
			int ch = tr.text[p];
			if (Character.isHighSurrogate((char) ch) && p < tr.limit - 1 && Character.isLowSurrogate(tr.text[++p])) {
				// rare case
				ch = Character.toCodePoint((char) ch, tr.text[p]); // inc
				w = fm.getFloatWidth(ch);
			} else if (ch > 255) {
				// unicode value
				w = fm.getFloatWidth(ch);
			} else {
				w = fm.getWidthsFloat()[ch];

			}

			data._indices[g] = p + baseIndex;
			data._positions[g2++] = x;
			data._positions[g2++] = y;
			data._glyphs[g] = ch;
			data._count++;
			x += w;
		}
		data._positions[g2++] = x;
		data._positions[g2++] = y;

	}


    
    //
//    private static native void
//        nativeLayout(Font2D font, FontStrike strike, float[] mat, int gmask,
//             int baseIndex, char[] chars, int offset, int limit,
//             int min, int max, int script, int lang, int typo_flags,
//             Point2D.Float pt, GVData data, long upem, long layoutTables);


}
