/*******************************************************************************
 * Copyright (c) 2007 java2script.org and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Zhou Renjian - initial API and implementation
 *******************************************************************************/

package java.text;

import java.util.Locale;


/**
 * @author zhou renjian
 *
 * 2006-10-10
 */
public class MessageFormat {
	private String pattern;
	
    public MessageFormat(String pattern) {
    	this.pattern = pattern;
    }
    
    public MessageFormat(String pattern, Locale locale) {
    	this.pattern = pattern;
    }

    public static String format(String pattern, Object... args) {
        /**
         * @j2sNative
         * return pattern.replace (/\{(\d+)\}/g, function ($0, $1) {
         * 	var i = parseInt ($1);
         * 	if (args == null) return null;
         *  return args[i];
         * });
         */ 
    	{
    	return pattern;
         }
    }
    
    public final String format (Object obj) {
    	return format(pattern, new Object[] { obj });
    }
}
