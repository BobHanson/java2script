/*
 * @(#)Locale.java	1.69 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * (C) Copyright Taligent, Inc. 1996, 1997 - All Rights Reserved
 * (C) Copyright IBM Corp. 1996 - 1998 - All Rights Reserved
 *
 * The original version of this source code and documentation
 * is copyrighted and owned by Taligent, Inc., a wholly-owned
 * subsidiary of IBM. These materials are provided under terms
 * of a License Agreement between Taligent and Sun. This technology
 * is protected by multiple US and International patents.
 *
 * This notice and attribution to Taligent may not be removed.
 * Taligent is a registered trademark of Taligent, Inc.
 *
 */

package java.util;

import java.io.Serializable;

/**
 *
 * A <code>Locale</code> object represents a specific geographical, political,
 * or cultural region. An operation that requires a <code>Locale</code> to perform
 * its task is called <em>locale-sensitive</em> and uses the <code>Locale</code>
 * to tailor information for the user. For example, displaying a number
 * is a locale-sensitive operation--the number should be formatted
 * according to the customs/conventions of the user's native country,
 * region, or culture.
 *
 * <P>
 * Create a <code>Locale</code> object using the constructors in this class:
 * <blockquote>
 * <pre>
 * Locale(String language)
 * Locale(String language, String country)
 * Locale(String language, String country, String variant)
 * </pre>
 * </blockquote>
 * The language argument is a valid <STRONG>ISO Language Code.</STRONG> 
 * These codes are the lower-case, two-letter codes as defined by ISO-639.
 * You can find a full list of these codes at a number of sites, such as:
 * <BR><a href ="http://www.ics.uci.edu/pub/ietf/http/related/iso639.txt">
 * <code>http://www.ics.uci.edu/pub/ietf/http/related/iso639.txt</code></a>
 *
 * <P>
 * The country argument is a valid <STRONG>ISO Country Code.</STRONG> These 
 * codes are the upper-case, two-letter codes as defined by ISO-3166.
 * You can find a full list of these codes at a number of sites, such as:
 * <BR><a href="http://www.chemie.fu-berlin.de/diverse/doc/ISO_3166.html">
 * <code>http://www.chemie.fu-berlin.de/diverse/doc/ISO_3166.html</code></a>
 *
 * <P>
 * The variant argument is a vendor or browser-specific code.
 * For example, use WIN for Windows, MAC for Macintosh, and POSIX for POSIX.
 * Where there are two variants, separate them with an underscore, and
 * put the most important one first. For example, a Traditional Spanish collation 
 * might construct a locale with parameters for language, country and variant as: 
 * "es", "ES", "Traditional_WIN".
 *
 * <P>
 * Because a <code>Locale</code> object is just an identifier for a region,
 * no validity check is performed when you construct a <code>Locale</code>.
 * If you want to see whether particular resources are available for the
 * <code>Locale</code> you construct, you must query those resources. For
 * example, ask the <code>NumberFormat</code> for the locales it supports
 * using its <code>getAvailableLocales</code> method.
 * <BR><STRONG>Note:</STRONG> When you ask for a resource for a particular
 * locale, you get back the best available match, not necessarily
 * precisely what you asked for. For more information, look at
 * {@link ResourceBundle}.
 *
 * <P>
 * The <code>Locale</code> class provides a number of convenient constants
 * that you can use to create <code>Locale</code> objects for commonly used
 * locales. For example, the following creates a <code>Locale</code> object
 * for the United States:
 * <blockquote>
 * <pre>
 * Locale.US
 * </pre>
 * </blockquote>
 *
 * <P>
 * Once you've created a <code>Locale</code> you can query it for information about
 * itself. Use <code>getCountry</code> to get the ISO Country Code and
 * <code>getLanguage</code> to get the ISO Language Code. You can
 * use <code>getDisplayCountry</code> to get the
 * name of the country suitable for displaying to the user. Similarly,
 * you can use <code>getDisplayLanguage</code> to get the name of
 * the language suitable for displaying to the user. Interestingly,
 * the <code>getDisplayXXX</code> methods are themselves locale-sensitive
 * and have two versions: one that uses the default locale and one
 * that uses the locale specified as an argument.
 *
 * <P>
 * The Java 2 platform provides a number of classes that perform locale-sensitive
 * operations. For example, the <code>NumberFormat</code> class formats
 * numbers, currency, or percentages in a locale-sensitive manner. Classes
 * such as <code>NumberFormat</code> have a number of convenience methods
 * for creating a default object of that type. For example, the
 * <code>NumberFormat</code> class provides these three convenience methods
 * for creating a default <code>NumberFormat</code> object:
 * <blockquote>
 * <pre>
 * NumberFormat.getInstance()
 * NumberFormat.getCurrencyInstance()
 * NumberFormat.getPercentInstance()
 * </pre>
 * </blockquote>
 * These methods have two variants; one with an explicit locale
 * and one without; the latter using the default locale.
 * <blockquote>
 * <pre>
 * NumberFormat.getInstance(myLocale)
 * NumberFormat.getCurrencyInstance(myLocale)
 * NumberFormat.getPercentInstance(myLocale)
 * </pre>
 * </blockquote>
 * A <code>Locale</code> is the mechanism for identifying the kind of object
 * (<code>NumberFormat</code>) that you would like to get. The locale is
 * <STRONG>just</STRONG> a mechanism for identifying objects,
 * <STRONG>not</STRONG> a container for the objects themselves.
 *
 * <P>
 * Each class that performs locale-sensitive operations allows you
 * to get all the available objects of that type. You can sift
 * through these objects by language, country, or variant,
 * and use the display names to present a menu to the user.
 * For example, you can create a menu of all the collation objects
 * suitable for a given language. Such classes must implement these
 * three class methods:
 * <blockquote>
 * <pre>
 * public static Locale[] getAvailableLocales()
 * public static String getDisplayName(Locale objectLocale,
 *                                     Locale displayLocale)
 * public static final String getDisplayName(Locale objectLocale)
 *     // getDisplayName will throw MissingResourceException if the locale
 *     // is not one of the available locales.
 * </pre>
 * </blockquote>
 *
 * @see         ResourceBundle
 * @see         java.text.Format
 * @see         java.text.NumberFormat
 * @see         java.text.Collator
 * @version     1.69, 01/23/03
 * @author      Mark Davis
 * @since       1.1
 */

public final class Locale implements Cloneable, Serializable {

    /** Useful constant for language.
     */
    static public final Locale ENGLISH = new Locale("en","","");

    /** Useful constant for language.
     */
    static public final Locale FRENCH = new Locale("fr","","");

    /** Useful constant for language.
     */
    static public final Locale GERMAN = new Locale("de","","");

    /** Useful constant for language.
     */
    static public final Locale ITALIAN = new Locale("it","","");

    /** Useful constant for language.
     */
    static public final Locale JAPANESE = new Locale("ja","","");

    /** Useful constant for language.
     */
    static public final Locale KOREAN = new Locale("ko","","");

    /** Useful constant for language.
     */
    static public final Locale CHINESE = new Locale("zh","","");

    /** Useful constant for language.
     */
    static public final Locale SIMPLIFIED_CHINESE = new Locale("zh","CN","");

    /** Useful constant for language.
     */
    static public final Locale TRADITIONAL_CHINESE = new Locale("zh","TW","");

    /** Useful constant for country.
     */
    static public final Locale FRANCE = new Locale("fr","FR","");

    /** Useful constant for country.
     */
    static public final Locale GERMANY = new Locale("de","DE","");

    /** Useful constant for country.
     */
    static public final Locale ITALY = new Locale("it","IT","");

    /** Useful constant for country.
     */
    static public final Locale JAPAN = new Locale("ja","JP","");

    /** Useful constant for country.
     */
    static public final Locale KOREA = new Locale("ko","KR","");

    /** Useful constant for country.
     */
    static public final Locale CHINA = new Locale("zh","CN","");

    /** Useful constant for country.
     */
    static public final Locale PRC = new Locale("zh","CN","");

    /** Useful constant for country.
     */
    static public final Locale TAIWAN = new Locale("zh","TW","");

    /** Useful constant for country.
     */
    static public final Locale UK = new Locale("en","GB","");

    /** Useful constant for country.
     */
    static public final Locale US = new Locale("en","US","");

    /** Useful constant for country.
     */
    static public final Locale CANADA = new Locale("en","CA","");

    /** Useful constant for country.
     */
    static public final Locale CANADA_FRENCH = new Locale("fr","CA","");

    /** serialization ID
     */
    static final long serialVersionUID = 9149081749638150636L;

    /**
     * Construct a locale from language, country, variant.
     * NOTE:  ISO 639 is not a stable standard; some of the language codes it defines
     * (specifically iw, ji, and in) have changed.  This constructor accepts both the
     * old codes (iw, ji, and in) and the new codes (he, yi, and id), but all other
     * API on Locale will return only the OLD codes.
     * @param language lowercase two-letter ISO-639 code.
     * @param country uppercase two-letter ISO-3166 code.
     * @param variant vendor and browser specific code. See class description.
     * @exception NullPointerException thrown if any argument is null.
     */
    public Locale(String language, String country, String variant) {
        this.language = convertOldISOCodes(language);
        this.country = country.toUpperCase(); //toUpperCase(country).intern();
        this.variant = variant.intern();
    }

    /**
     * Construct a locale from language, country.
     * NOTE:  ISO 639 is not a stable standard; some of the language codes it defines
     * (specifically iw, ji, and in) have changed.  This constructor accepts both the
     * old codes (iw, ji, and in) and the new codes (he, yi, and id), but all other
     * API on Locale will return only the OLD codes.
     * @param language lowercase two-letter ISO-639 code.
     * @param country uppercase two-letter ISO-3166 code.
     * @exception NullPointerException thrown if either argument is null.
     */
    public Locale(String language, String country) {
        this(language, country, "");
    }

    /**
     * Construct a locale from a language code.
     * NOTE:  ISO 639 is not a stable standard; some of the language codes it defines
     * (specifically iw, ji, and in) have changed.  This constructor accepts both the
     * old codes (iw, ji, and in) and the new codes (he, yi, and id), but all other
     * API on Locale will return only the OLD codes.
     * @param language lowercase two-letter ISO-639 code.
     * @exception NullPointerException thrown if argument is null.
     * @since 1.4
     */
    public Locale(String language) {
        this(language, "", "");
    }


    /**
     * Gets the current value of the default locale for this instance
     * of the Java Virtual Machine.
     * <p>
     * The Java Virtual Machine sets the default locale during startup
     * based on the host environment. It is used by many locale-sensitive
     * methods if no locale is explicitly specified.
     * It can be changed using the
     * {@link #setDefault(java.util.Locale) setDefault} method.
     *
     * @return the default locale for this instance of the Java Virtual Machine
     */
    public static Locale getDefault() {
        // do not synchronize this method - see 4071298
        // it's OK if more than one default locale happens to be created
        if (defaultLocale == null)
        {
          String language = "en", country = "US", variant = ""; //region, 

            /**
             * @j2sNative
             * navigator.userAgent.replace (/;\s*([a-zA-Z]{2,})[-_]([a-zA-Z]{2,});/, function ($0, $1, $2) {
             * 	language = $1;
             * 	country = $2;
             * });
             */
        	{
//            String language, country, variant; //region, 
//            language = null; /* (String) AccessController.doPrivileged(
//                            new GetPropertyAction("user.language", "en")); */
//            // for compatibility, check for old user.region property
//            //region = null; /* (String) AccessController.doPrivileged(
//              //              new GetPropertyAction("user.region")); */
//            if (region != null) {
//                // region can be of form country, country_variant, or _variant
//                int i = region.indexOf('_');
//                if (i >= 0) {
//                    country = region.substring(0, i);
//                    variant = region.substring(i + 1);
//                } else {
//                    country = region;
//                    variant = "";
//                }
//            } else 
//            {
//                country = null; /* (String) AccessController.doPrivileged(
//                                new GetPropertyAction("user.country", "")); */
//                variant = null; /* (String) AccessController.doPrivileged(
//                                new GetPropertyAction("user.variant", "")); */
//            }
        	}
            defaultLocale = new Locale(language, country, variant);
       }
        return defaultLocale;
    }

    /**
     * Sets the default locale for this instance of the Java Virtual Machine.
     * This does not affect the host locale.
     * <p>
     * If there is a security manager, its <code>checkPermission</code>
     * method is called with a <code>PropertyPermission("user.language", "write")</code>
     * permission before the default locale is changed.
     * <p>
     * The Java Virtual Machine sets the default locale during startup
     * based on the host environment. It is used by many locale-sensitive
     * methods if no locale is explicitly specified.
     * <p>
     * Since changing the default locale may affect many different areas
     * of functionality, this method should only be used if the caller
     * is prepared to reinitialize locale-sensitive code running
     * within the same Java Virtual Machine, such as the user interface.
     *
     * @throws SecurityException
     *        if a security manager exists and its
     *        <code>checkPermission</code> method doesn't allow the operation.
     * @throws NullPointerException if <code>newLocale</code> is null
     * @param newLocale the new default locale
     * @see SecurityManager#checkPermission
     * @see java.util.PropertyPermission
     */
    public static synchronized void setDefault(Locale newLocale) {
        if (newLocale == null)
            throw new NullPointerException("Can't set default locale to NULL");

        /**
         * @j2sNative java.util.Locale.defaultLocale = newLocale; 
         */ {
        SecurityManager sm = System.getSecurityManager();
        if (sm != null) sm.checkPermission(new PropertyPermission
                        ("user.language", "write"));
            defaultLocale = newLocale;
        }
    }

    /**
     * Returns a list of all installed locales.
     * 
     * @j2sNative
var lcl = java.util.Locale; 
return [
	lcl.ENGLISH,
	lcl.ENGLISH,
	lcl.FRENCH,
	lcl.GERMAN,
	lcl.ITALIAN,
	lcl.JAPANESE,
	lcl.KOREAN,
	lcl.CHINESE,
	lcl.SIMPLIFIED_CHINESE,
	lcl.TRADITIONAL_CHINESE,
	lcl.FRANCE,
	lcl.GERMANY,
	lcl.ITALY,
	lcl.JAPAN,
	lcl.KOREA,
	lcl.CHINA,
	lcl.PRC,
	lcl.TAIWAN,
	lcl.UK,
	lcl.US,
	lcl.CANADA,
	lcl.CANADA_FRENCH
];
     */
    public static Locale[] getAvailableLocales() {
        return null; // LocaleData.getAvailableLocales("LocaleString");
    }

    /**
     * Returns a list of all 2-letter country codes defined in ISO 3166.
     * Can be used to create Locales.
     */
    public static String[] getISOCountries() {
        if (isoCountries == null) {
            isoCountries = new String[compressedIsoCountries.length() / 6];
            for (int i = 0; i < isoCountries.length; i++)
                isoCountries[i] = compressedIsoCountries.substring((i * 6) + 1, (i * 6) + 3);
        }
        String[] result = new String[isoCountries.length];
        System.arraycopy(isoCountries, 0, result, 0, isoCountries.length);
        return result;
    }

    /**
     * Returns a list of all 2-letter language codes defined in ISO 639.
     * Can be used to create Locales.
     * [NOTE:  ISO 639 is not a stable standard-- some languages' codes have changed.
     * The list this function returns includes both the new and the old codes for the
     * languages whose codes have changed.]
     */
    public static String[] getISOLanguages() {
        if (isoLanguages == null) {
            isoLanguages = new String[compressedIsoLanguages.length() / 6];
            for (int i = 0; i < isoLanguages.length; i++)
                isoLanguages[i] = compressedIsoLanguages.substring((i * 6) + 1, (i * 6) + 3);
        }
        String[] result = new String[isoLanguages.length];
        System.arraycopy(isoLanguages, 0, result, 0, isoLanguages.length);
        return result;
    }

    /**
     * Returns the language code for this locale, which will either be the empty string
     * or a lowercase ISO 639 code.
     * <p>NOTE:  ISO 639 is not a stable standard-- some languages' codes have changed.
     * Locale's constructor recognizes both the new and the old codes for the languages
     * whose codes have changed, but this function always returns the old code.  If you
     * want to check for a specific language whose code has changed, don't do <pre>
     * if (locale.getLanguage().equals("he")
     *    ...
     * </pre>Instead, do<pre>
     * if (locale.getLanguage().equals(new Locale("he", "", "").getLanguage())
     *    ...</pre>
     * @see #getDisplayLanguage
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Returns the country/region code for this locale, which will either be the empty string
     * or an upercase ISO 3166 2-letter code.
     * @see #getDisplayCountry
     */
    public String getCountry() {
        return country;
    }

    /**
     * Returns the variant code for this locale.
     * @see #getDisplayVariant
     */
    public String getVariant() {
        return variant;
    }

    /**
     * Getter for the programmatic name of the entire locale,
     * with the language, country and variant separated by underbars.
     * Language is always lower case, and country is always upper case.
     * If the language is missing, the string will begin with an underbar.
     * If both the language and country fields are missing, this function
     * will return the empty string, even if the variant field is filled in
     * (you can't have a locale with just a variant-- the variant must accompany
     * a valid language or country code).
     * Examples: "en", "de_DE", "_GB", "en_US_WIN", "de__POSIX", "fr__MAC"
     * @see #getDisplayName
     */
    @Override
	public final String toString() {
        boolean l = language.length() != 0;
        boolean c = country.length() != 0;
        boolean v = variant.length() != 0;
        /*
        StringBuffer result = new StringBuffer(language);
        if (c||(l&&v)) {
            result += ('_') += (country); // This may just append '_'
        }
        if (v&&(l||c)) {
            result += ('_') += (variant);
        }
        return result;
        */
        String result = language;
        if (c||(l&&v)) {
            result += '_' + country; // This may just append '_'
        }
        if (v&&(l||c)) {
            result += '_' + variant;
        }
        return result;
    }

    /**
     * Returns a three-letter abbreviation for this locale's language.  If the locale
     * doesn't specify a language, this will be the empty string.  Otherwise, this will
     * be a lowercase ISO 639-2/T language code.
     * The ISO 639-2 language codes can be found on-line at
     *   <a href="ftp://dkuug.dk/i18n/iso-639-2.txt"><code>ftp://dkuug.dk/i18n/iso-639-2.txt</code></a>
     * @exception MissingResourceException Throws MissingResourceException if the
     * three-letter language abbreviation is not available for this locale.
     */
    public String getISO3Language() throws MissingResourceException {
        int length = language.length();

        if (length == 0) {
            return "";
        }

        int index = compressedIsoLanguages.indexOf("," + language);
        if (index == -1 || length != 2) {
            throw new MissingResourceException("Couldn't find 3-letter language code for "
                    + language, "LocaleElements_" + toString(), "ShortLanguage");
        }
        return compressedIsoLanguages.substring(index + 3, index + 6);
    }

    /**
     * Returns a three-letter abbreviation for this locale's country.  If the locale
     * doesn't specify a country, this will be tbe the empty string.  Otherwise, this will
     * be an uppercase ISO 3166 3-letter country code.
     * @exception MissingResourceException Throws MissingResourceException if the
     * three-letter country abbreviation is not available for this locale.
     */
    public String getISO3Country() throws MissingResourceException {
        int length = country.length();

        if (length == 0) {
            return "";
        }

        int index = compressedIsoCountries.indexOf("," + country);
        if (index == -1 || length != 2) {
            throw new MissingResourceException("Couldn't find 3-letter country code for "
                    + country, "LocaleElements_" + toString(), "ShortCountry");
        }
        return compressedIsoCountries.substring(index + 3, index + 6);
    }

    /**
     * Returns a name for the locale's language that is appropriate for display to the
     * user.
     * If possible, the name returned will be localized for the default locale.
     * For example, if the locale is fr_FR and the default locale
     * is en_US, getDisplayLanguage() will return "French"; if the locale is en_US and
     * the default locale is fr_FR, getDisplayLanguage() will return "anglais".
     * If the name returned cannot be localized for the default locale,
     * (say, we don't have a Japanese name for Croatian),
     * this function falls back on the English name, and uses the ISO code as a last-resort
     * value.  If the locale doesn't specify a language, this function returns the empty string.
     */
    public final String getDisplayLanguage() {
        return getDisplayLanguage(getDefault());
    }

    /**
     * Returns a name for the locale's language that is appropriate for display to the
     * user.
     * If possible, the name returned will be localized according to inLocale.
     * For example, if the locale is fr_FR and inLocale
     * is en_US, getDisplayLanguage() will return "French"; if the locale is en_US and
     * inLocale is fr_FR, getDisplayLanguage() will return "anglais".
     * If the name returned cannot be localized according to inLocale,
     * (say, we don't have a Japanese name for Croatian),
     * this function falls back on the default locale, on the English name, and finally
     * on the ISO code as a last-resort value.  If the locale doesn't specify a language,
     * this function returns the empty string.
     * 
     * @j2sNative
     * return inLocale.language;
     */
    public String getDisplayLanguage(Locale inLocale) {
//        String  langCode = language;
//        if (langCode.length() == 0)
//            return "";
//
//        Locale  workingLocale = (Locale)inLocale.clone();
//        String  result = null;
//        int     phase = 0;
//        boolean done = false;
//
//        if (workingLocale.variant.length() == 0)
//            phase = 1;
//        if (workingLocale.country.length() == 0)
//            phase = 2;
//
//        while (!done) {
//            try {
//                ResourceBundle bundle = null; // LocaleData.getLocaleElements(workingLocale);
//                result = findStringMatch((String[][])bundle.getObject("Languages"),
//                                    langCode, langCode);
//                if (result.length() != 0)
//                    done = true;
//            }
//            catch (Exception e) {
//                // just fall through
//            }
//
//            if (!done) {
//                switch (phase) {
//                    case 0:
//                        workingLocale.variant = "";
//                        break;
//
//                    case 1:
//                        workingLocale.country = "";
//                        break;
//
//                    case 2:
//                        workingLocale = getDefault();
//                        break;
//
//                    case 3:
//                        workingLocale = new Locale("", "", "");
//                        break;
//
//                    default:
//                        return langCode;
//                }
//                phase++;
//            }
//        }
        return null;
    }

    /**
     * Returns a name for the locale's country that is appropriate for display to the
     * user.
     * If possible, the name returned will be localized for the default locale.
     * For example, if the locale is fr_FR and the default locale
     * is en_US, getDisplayCountry() will return "France"; if the locale is en_US and
     * the default locale is fr_FR, getDisplayLanguage() will return "Etats-Unis".
     * If the name returned cannot be localized for the default locale,
     * (say, we don't have a Japanese name for Croatia),
     * this function falls back on the English name, and uses the ISO code as a last-resort
     * value.  If the locale doesn't specify a country, this function returns the empty string.
     */
    public final String getDisplayCountry() {
        return getDisplayCountry(getDefault());
    }

    /**
     * Returns a name for the locale's country that is appropriate for display to the
     * user.
     * If possible, the name returned will be localized according to inLocale.
     * For example, if the locale is fr_FR and inLocale
     * is en_US, getDisplayCountry() will return "France"; if the locale is en_US and
     * inLocale is fr_FR, getDisplayLanguage() will return "Etats-Unis".
     * If the name returned cannot be localized according to inLocale.
     * (say, we don't have a Japanese name for Croatia),
     * this function falls back on the default locale, on the English name, and finally
     * on the ISO code as a last-resort value.  If the locale doesn't specify a country,
     * this function returns the empty string.
     * 
     * @j2sNative
     * return inLocale.country;
     */
    public String getDisplayCountry(Locale inLocale) {
//        String  ctryCode = country;
//        if (ctryCode.length() == 0)
//            return "";
//
//        Locale  workingLocale = (Locale)inLocale.clone();
//        String  result = null;
//        int     phase = 0;
//        boolean done = false;
//
//        if (workingLocale.variant.length() == 0)
//            phase = 1;
//        if (workingLocale.country.length() == 0)
//            phase = 2;
//
//        while (!done) {
//            try {
//                ResourceBundle bundle = null; // LocaleData.getLocaleElements(workingLocale);
//                result = findStringMatch((String[][])bundle.getObject("Countries"),
//                                    ctryCode, ctryCode);
//                if (result.length() != 0)
//                    done = true;
//            }
//            catch (Exception e) {
//                // just fall through
//            }
//
//            if (!done) {
//                switch (phase) {
//                    case 0:
//                        workingLocale.variant = "";
//                        break;
//
//                    case 1:
//                        workingLocale.country = "";
//                        break;
//
//                    case 2:
//                        workingLocale = getDefault();
//                        break;
//
//                    case 3:
//                        workingLocale = new Locale("", "", "");
//                        break;
//
//                    default:
//                        return ctryCode;
//                }
//                phase++;
//            }
//        }
        return null;
    }

    /**
     * Returns a name for the locale's variant code that is appropriate for display to the
     * user.  If possible, the name will be localized for the default locale.  If the locale
     * doesn't specify a variant code, this function returns the empty string.
     */
    public final String getDisplayVariant() {
        return getDisplayVariant(getDefault());
    }

    /**
     * Returns a name for the locale's variant code that is appropriate for display to the
     * user.  If possible, the name will be localized for inLocale.  If the locale
     * doesn't specify a variant code, this function returns the empty string.
     * 
     * @j2sNative
     * return inLocale.variant;
     */
    public String getDisplayVariant(Locale inLocale) {
//        if (variant.length() == 0)
            return "";
//
//        ResourceBundle bundle = null; // LocaleData.getLocaleElements(inLocale);
//
//        String names[] = getDisplayVariantArray(bundle);
//
//        // Get the localized patterns for formatting a list, and use
//        // them to format the list.
//        String[] patterns;
//        try {
//            patterns = (String[])bundle.getObject("LocaleNamePatterns");
//        }
//        catch (MissingResourceException e) {
//            patterns = null;
//        }
//        return formatList(patterns, names);
    }

    /**
     * Returns a name for the locale that is appropriate for display to the
     * user.  This will be the values returned by getDisplayLanguage(), getDisplayCountry(),
     * and getDisplayVariant() assembled into a single string.  The display name will have
     * one of the following forms:<p><blockquote>
     * language (country, variant)<p>
     * language (country)<p>
     * language (variant)<p>
     * country (variant)<p>
     * language<p>
     * country<p>
     * variant<p></blockquote>
     * depending on which fields are specified in the locale.  If the language, country,
     * and variant fields are all empty, this function returns the empty string.
     */
    public final String getDisplayName() {
        return getDisplayName(getDefault());
    }

    /**
     * Returns a name for the locale that is appropriate for display to the
     * user.  This will be the values returned by getDisplayLanguage(), getDisplayCountry(),
     * and getDisplayVariant() assembled into a single string.  The display name will have
     * one of the following forms:<p><blockquote>
     * language (country, variant)<p>
     * language (country)<p>
     * language (variant)<p>
     * country (variant)<p>
     * language<p>
     * country<p>
     * variant<p></blockquote>
     * depending on which fields are specified in the locale.  If the language, country,
     * and variant fields are all empty, this function returns the empty string.
     * 
     * @j2sNative
     * var s = inLocale.language + "_" + inLocale.country;
     * var v = inLocale.variant;
     * if (v != null && v.length != 0) {
     * 	return s + "(" + v + ")";
     * } else {
     * 	return s;
     * }
     */
    public String getDisplayName(Locale inLocale) {
//        ResourceBundle bundle = null; // LocaleData.getLocaleElements(inLocale);
//
//        String languageName = getDisplayLanguage(inLocale);
//        String countryName = getDisplayCountry(inLocale);
//        String[] variantNames = getDisplayVariantArray(bundle);
//
//        // Get the localized patterns for formatting a display name.
//        String[] patterns;
//        try {
//            patterns = (String[])bundle.getObject("LocaleNamePatterns");
//        }
//        catch (MissingResourceException e) {
//            patterns = null;
//        }
//
//        // The display name consists of a main name, followed by qualifiers.
//        // Typically, the format is "MainName (Qualifier, Qualifier)" but this
//        // depends on what pattern is stored in the display locale.
//        String   mainName       = null;
//        String[] qualifierNames = null;
//
//        // The main name is the language, or if there is no language, the country.
//        // If there is neither language nor country (an anomalous situation) then
//        // the display name is simply the variant's display name.
//        if (languageName.length() != 0) {
//            mainName = languageName;
//            if (countryName.length() != 0) {
//                qualifierNames = new String[variantNames.length + 1];
//                System.arraycopy(variantNames, 0, qualifierNames, 1, variantNames.length);
//                qualifierNames[0] = countryName;
//            }
//            else qualifierNames = variantNames;
//        }
//        else if (countryName.length() != 0) {
//            mainName = countryName;
//            qualifierNames = variantNames;
//        }
//        else {
//            return formatList(patterns, variantNames);
//        }
//
//        // Create an array whose first element is the number of remaining
//        // elements.  This serves as a selector into a ChoiceFormat pattern from
//        // the resource.  The second and third elements are the main name and
//        // the qualifier; if there are no qualifiers, the third element is
//        // unused by the format pattern.
//        Object[] displayNames = {
//            new Integer(qualifierNames.length != 0 ? 2 : 1),
//            mainName,
//            // We could also just call formatList() and have it handle the empty
//            // list case, but this is more efficient, and we want it to be
//            // efficient since all the language-only locales will not have any
//            // qualifiers.
//            qualifierNames.length != 0 ? formatList(patterns, qualifierNames) : null
//        };
//
//        if (patterns != null) {
//            return new MessageFormat(patterns[0]).format(displayNames);
//        }
//        else {
//            // If we cannot get the message format pattern, then we use a simple
//            // hard-coded pattern.  This should not occur in practice unless the
//            // installation is missing some core files (LocaleElements etc.).
//            String result = "";
//            result += ((String)displayNames[1]);
//            if (displayNames.length > 2) {
//                result += (" (");
//                result += ((String)displayNames[2]);
//                result += (")");
//            }
//            return result;
//        }
    	return null;
    }

    /**
     * Overrides Cloneable
     */
    @Override
	public Object clone()
    {
        try {
            Locale that = (Locale)super.clone();
            return that;
        } catch (CloneNotSupportedException e) {
            throw new InternalError();
        }
    }

    /**
     * Override hashCode.
     * Since Locales are often used in hashtables, caches the value
     * for speed.
     */
      // XXX Depending on performance of synchronized, may want to
      // XXX just compute in constructor.
    @Override
	public synchronized int hashCode() {
        if (hashcode == -1) {
            hashcode =
        language.hashCode() ^
        country.hashCode() ^
        variant.hashCode();
        }
        return hashcode;
    }

    // Overrides

    /**
     * Returns true if this Locale is equal to another object.  A Locale is
     * deemed equal to another Locale with identical language, country,
     * and variant, and unequal to all other objects.
     *
     * @return true if this Locale is equal to the specified object.
     */

    @Override
	public boolean equals(Object obj) {
        if (this == obj)                      // quick check
            return true;
        if (!(obj instanceof Locale))         // (1) same object?
            return false;
        Locale other = (Locale) obj;
        if (hashCode() != other.hashCode()) return false;       // quick check
        if (language != other.language) return false;
        if (country != other.country) return false;
        if (variant != other.variant) return false;
        return true; // we made it through the guantlet.
        // (1)  We don't check super.equals since it is Object.
        //      Since Locale is final, we don't have to check both directions.
    }

    // ================= privates =====================================

    // XXX instance and class variables. For now keep these separate, since it is
    // faster to match. Later, make into single string.

    /**
     * @serial
     * @see #getLanguage
     */
    private String language = "";

    /**
     * @serial
     * @see #getCountry
     */
    private String country = "";

    /**
     * @serial
     * @see #getVariant
     */
    private String variant = "";

    /**
     * Placeholder for the object's hash code.  Always -1.
     * @serial
     */
    private int hashcode = -1;        // lazy evaluated

    private static Locale defaultLocale = null;

//    /**
//     * Return an array of the display names of the variant.
//     * @param bundle the ResourceBundle to use to get the display names
//     * @return an array of display names, possible of zero length.
//     * 
//     * @j2sIgnore
//     */
//    private String[] getDisplayVariantArray(ResourceBundle bundle) {
//        // Split the variant name into tokens separated by '_'.
//        StringTokenizer tokenizer = new StringTokenizer(variant, "_");
//        String[] names = new String[tokenizer.countTokens()];
//
//        // For each variant token, lookup the display name.  If
//        // not found, use the variant name itself.
//        for (int i=0; i<names.length; ++i) {
//            String token = tokenizer.nextToken();
//            try {
//                names[i] = (String)bundle.getObject("%%" + token);
//            }
//            catch (MissingResourceException e) {
//                names[i] = token;
//            }
//        }
//        return names;
//    }
//
//    /**
//     * Format a list with an array of patterns.
//     * @param patterns an array of three patterns. The first pattern is not
//     * used. The second pattern should create a MessageFormat taking 0-3 arguments
//     * and formatting them into a list. The third pattern should take 2 arguments
//     * and is used by composeList. If patterns is null, then a the list is
//     * formatted by concatenation with the delimiter ','.
//     * @param stringList the list of strings to be formatted.
//     * @return a string representing the list.
//     * 
//     * @j2sIgnore
//     */
//    private static String formatList(String[] patterns, String[] stringList) {
//        // If we have no list patterns, compose the list in a simple,
//        // non-localized way.
//        if (patterns == null) {
//            String result = "";
//            for (int i=0; i<stringList.length; ++i) {
//                if (i>0) result += (',');
//                result += (stringList[i]);
//            }
//            return result;
//        }
//
//        // Compose the list down to three elements if necessary
//        if (stringList.length > 3) {
//            MessageFormat format = new MessageFormat(patterns[2]);
//            stringList = composeList(format, stringList);
//        }
//
//        // Rebuild the argument list with the list length as the first element
//        Object[] args = new Object[stringList.length + 1];
//        System.arraycopy(stringList, 0, args, 1, stringList.length);
//        args[0] = new Integer(stringList.length);
//
//        // Format it using the pattern in the resource
//        MessageFormat format = new MessageFormat(patterns[1]);
//        return format.format(args);
//    }
//
//    /**
//     * Given a list of strings, return a list shortened to three elements.
//     * Shorten it by applying the given format to the first two elements
//     * recursively.
//     * @param format a format which takes two arguments
//     * @param list a list of strings
//     * @return if the list is three elements or shorter, the same list;
//     * otherwise, a new list of three elements.
//     * 
//     * @j2sIgnore
//     */
//    private static String[] composeList(MessageFormat format, String[] list) {
//        if (list.length <= 3) return list;
//
//        // Use the given format to compose the first two elements into one
//        String[] listItems = { list[0], list[1] };
//        String newItem = format.format(listItems);
//
//        // Form a new list one element shorter
//        String[] newList = new String[list.length-1];
//        System.arraycopy(list, 2, newList, 1, newList.length-1);
//        newList[0] = newItem;
//
//        // Recurse
//        return composeList(format, newList);
//    }
//
//    /**
//     * @serialData The first three fields are three <code>String</code> objects:
//     * the first is a 2-letter ISO 639 code representing the locale's language,
//     * the second is a 2-letter ISO 3166 code representing the locale's region or
//     * country, and the third is an optional chain of variant codes defined by this
//     * library.  Any of the fields may be the empty string.  The fourth field is an
//     * <code>int</code> whose value is always -1.  This is a sentinel value indicating
//     * the <code>Locale</code>'s hash code must be recomputed.
//     * 
//     * @j2sIgnore
//     */
//    private void writeObject(ObjectOutputStream out) throws IOException {
//        // hashcode is semantically transient.  We couldn't define it as transient
//        // because versions of this class that DIDN'T declare it as transient have
//        // already shipped.  What we're doing here is making sure that the written-out
//        // version of hashcode is always -1, regardless of what's really stored there
//        // (we hold onto the original value just in case someone might want it again).
//        // Writing -1 ensures that version 1.1 Locales will always recalculate their
//        // hash codes after being streamed back in.  This is necessary because
//        // String.hashCode() calculates its hash code differently in 1.2 than it did
//        // in 1.1.
//        int temp = hashcode;
//        hashcode = -1;
//        out.defaultWriteObject();
//        hashcode = temp;
//    }
//
//    /**
//     * @serialData The first three fields are three <code>String</code> objects:
//     * the first is a 2-letter ISO 639 code representing the locale's language,
//     * the second is a 2-letter ISO 3166 code representing the locale's region or
//     * country, and the third is an optional chain of variant codes defined by this
//     * library.  Any of the fields may be the empty string.  The fourth field is an
//     * <code>int</code>representing the locale's hash code, but is ignored by
//     * <code>readObject()</code>.  Whatever this field's value, the hash code is
//     * initialized to -1, a sentinel value that indicates the hash code must be
//     * recomputed.
//     * 
//     * @j2sIgnore
//     */
//    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
//        // hashcode is semantically transient.  We couldn't define it as transient
//        // because versions of this class that DIDN'T declare is as transient have
//        // already shipped.  This code makes sure that whatever value for hashcode
//        // was written on the stream, we ignore it and recalculate it on demand.  This
//        // is necessary because String.hashCode() calculates is hash code differently
//        // in version 1.2 than it did in 1.1.
//        in.defaultReadObject();
//        hashcode = -1;
//        language = convertOldISOCodes(language);
//        country = country.intern();
//        variant = variant.intern();
//    }

    /**
     * List of all 2-letter language codes currently defined in ISO 639.
     * (Because the Java VM specification turns an array constant into executable code
     * that generates the array element by element, we keep the array in compressed
     * form in a single string and build the array from it at run time when requested.)
     * [We're now also using this table to store a mapping from 2-letter ISO language codes
     * to 3-letter ISO language codes.  Each group of characters consists of a comma, a
     * 2-letter code, and a 3-letter code.  We look up a 3-letter code by searching for
     * a comma followed by a 2-letter code and then getting the three letters following
     * the 2-letter code.]
     */
    private static String[] isoLanguages = null;
    private static String compressedIsoLanguages =
        ",aaaar,ababk,afafr,amamh,arara,asasm,ayaym,azaze,babak,bebel,bgbul,bhbih,bibis,bnben,"
        + "bobod,brbre,cacat,cocos,csces,cycym,dadan,dedeu,dzdzo,elell,eneng,eoepo,esspa,"
        + "etest,eueus,fafas,fifin,fjfij,fofao,frfra,fyfry,gagai,gdgdh,glglg,gngrn,guguj,"
        + "hahau,heheb,hihin,hrhrv,huhun,hyhye,iaina,idind,ieile,ikipk,inind,isisl,itita,"
        + "iuiku,iwheb,jajpn,jiyid,jwjaw,kakat,kkkaz,klkal,kmkhm,knkan,kokor,kskas,kukur,"
        + "kykir,lalat,lnlin,lolao,ltlit,lvlav,mgmlg,mimri,mkmkd,mlmal,mnmon,momol,mrmar,"
        + "msmsa,mtmlt,mymya,nanau,nenep,nlnld,nonor,ococi,omorm,orori,papan,plpol,pspus,"
        + "ptpor,quque,rmroh,rnrun,roron,rurus,rwkin,sasan,sdsnd,sgsag,shsrp,sisin,skslk,"
        + "slslv,smsmo,snsna,sosom,sqsqi,srsrp,ssssw,stsot,susun,svswe,swswa,tatam,tetel,"
        + "tgtgk,ththa,titir,tktuk,tltgl,tntsn,toton,trtur,tstso,tttat,twtwi,uguig,ukukr,"
        + "ururd,uzuzb,vivie,vovol,wowol,xhxho,yiyid,yoyor,zazha,zhzho,zuzul";

    /**
     * List of all 2-letter country codes currently defined in ISO 3166.
     * (Because the Java VM specification turns an array constant into executable code
     * that generates the array element by element, we keep the array in compressed
     * form in a single string and build the array from it at run time when requested.)
     * [We're now also using this table to store a mapping from 2-letter ISO country codes
     * to 3-letter ISO country codes.  Each group of characters consists of a comma, a
     * 2-letter code, and a 3-letter code.  We look up a 3-letter code by searching for
     * a comma followed by a 2-letter code and then getting the three letters following
     * the 2-letter code.]
     */
    private static String[] isoCountries = null;
    private static String compressedIsoCountries =
        ",ADAND,AEARE,AFAFG,AGATG,AIAIA,ALALB,AMARM,ANANT,AOAGO,AQATA,ARARG,ASASM,ATAUT,"
        + "AUAUS,AWABW,AZAZE,BABIH,BBBRB,BDBGD,BEBEL,BFBFA,BGBGR,BHBHR,BIBDI,BJBEN,BMBMU,"
        + "BNBRN,BOBOL,BRBRA,BSBHS,BTBTN,BVBVT,BWBWA,BYBLR,BZBLZ,CACAN,CCCCK,CFCAF,CGCOG,"
        + "CHCHE,CICIV,CKCOK,CLCHL,CMCMR,CNCHN,COCOL,CRCRI,CUCUB,CVCPV,CXCXR,CYCYP,CZCZE,"
        + "DEDEU,DJDJI,DKDNK,DMDMA,DODOM,DZDZA,ECECU,EEEST,EGEGY,EHESH,ERERI,ESESP,ETETH,"
        + "FIFIN,FJFJI,FKFLK,FMFSM,FOFRO,FRFRA,FXFXX,GAGAB,GBGBR,GDGRD,GEGEO,GFGUF,GHGHA,"
        + "GIGIB,GLGRL,GMGMB,GNGIN,GPGLP,GQGNQ,GRGRC,GSSGS,GTGTM,GUGUM,GWGNB,GYGUY,HKHKG,"
        + "HMHMD,HNHND,HRHRV,HTHTI,HUHUN,IDIDN,IEIRL,ILISR,ININD,IOIOT,IQIRQ,IRIRN,ISISL,"
        + "ITITA,JMJAM,JOJOR,JPJPN,KEKEN,KGKGZ,KHKHM,KIKIR,KMCOM,KNKNA,KPPRK,KRKOR,KWKWT,"
        + "KYCYM,KZKAZ,LALAO,LBLBN,LCLCA,LILIE,LKLKA,LRLBR,LSLSO,LTLTU,LULUX,LVLVA,LYLBY,"
        + "MAMAR,MCMCO,MDMDA,MGMDG,MHMHL,MKMKD,MLMLI,MMMMR,MNMNG,MOMAC,MPMNP,MQMTQ,MRMRT,"
        + "MSMSR,MTMLT,MUMUS,MVMDV,MWMWI,MXMEX,MYMYS,MZMOZ,NANAM,NCNCL,NENER,NFNFK,NGNGA,"
        + "NINIC,NLNLD,NONOR,NPNPL,NRNRU,NUNIU,NZNZL,OMOMN,PAPAN,PEPER,PFPYF,PGPNG,PHPHL,"
        + "PKPAK,PLPOL,PMSPM,PNPCN,PRPRI,PTPRT,PWPLW,PYPRY,QAQAT,REREU,ROROM,RURUS,RWRWA,"
        + "SASAU,SBSLB,SCSYC,SDSDN,SESWE,SGSGP,SHSHN,SISVN,SJSJM,SKSVK,SLSLE,SMSMR,SNSEN,"
        + "SOSOM,SRSUR,STSTP,SVSLV,SYSYR,SZSWZ,TCTCA,TDTCD,TFATF,TGTGO,THTHA,TJTJK,TKTKL,"
        + "TMTKM,TNTUN,TOTON,TPTMP,TRTUR,TTTTO,TVTUV,TWTWN,TZTZA,UAUKR,UGUGA,UMUMI,USUSA,"
        + "UYURY,UZUZB,VAVAT,VCVCT,VEVEN,VGVGB,VIVIR,VNVNM,VUVUT,WFWLF,WSWSM,YEYEM,YTMYT,"
        + "YUYUG,ZAZAF,ZMZMB,ZRZAR,ZWZWE";
//
//    /*
//     * Locale needs its own, locale insenitive version of toLowerCase to
//     * avoid circularity problems between Locale and String.
//     * The most straightforward algorithm is used. Look at optimizations later.
//     */
//    /*
//    private String toLowerCase(String str) {
//        char[] buf = str.toCharArray();
//        for (int i = 0; i < buf.length; i++) {
//            buf[i] = Character.toLowerCase( buf[i] );
//        }
//        return new String( buf );
//    }
//    */
//
//    /*
//     * Locale needs its own, locale insensitive version of toUpperCase to
//     * avoid circularity problems between Locale and String.
//     * The most straightforward algorithm is used. Look at optimizations later.
//     */
//    /*
//    private String toUpperCase(String str) {
//        char[] buf = str.toCharArray();
//        for (int i = 0; i < buf.length; i++) {
//            buf[i] = Character.toUpperCase( buf[i] );
//        }
//        return new String( buf );
//    }
//    */
//
//    /**
//     * @j2sIgnore
//     */
//    private String findStringMatch(String[][] languages,
//                                   String desiredLanguage, String fallbackLanguage)
//    {
//        for (int i = 0; i < languages.length; ++i)
//            if (desiredLanguage.equals(languages[i][0]))
//                return languages[i][1];
//        if (!fallbackLanguage.equals(desiredLanguage))
//            for (int i = 0; i < languages.length; ++i)
//                if (fallbackLanguage.equals(languages[i][0]))
//                    return languages[i][1];
//        if (!"EN".equals(desiredLanguage) && "EN".equals(fallbackLanguage))
//            for (int i = 0; i < languages.length; ++i)
//                if ("EN".equals(languages[i][0]))
//                    return languages[i][1];
//        return "";
//    }

    private String convertOldISOCodes(String language) {
        // we accept both the old and the new ISO codes for the languages whose ISO
        // codes have changed, but we always store the OLD code, for backward compatibility
        language = language.toLowerCase(); //toLowerCase(language).intern();
        if (language == "he") {
            return "iw";
        } else if (language == "yi") {
            return "ji";
        } else if (language == "id") {
            return "in";
        } else {
            return language;
        }
    }
}
