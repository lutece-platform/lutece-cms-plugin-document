/*
 * Copyright (c) 2002-2020, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.document.utils;

import org.apache.commons.lang.StringUtils;


/**
 *
 * IntegerUtils
 *
 */
public final class IntegerUtils
{
    /** Default value in case the conversion fails */
    private static final int DEFAULT = -1;

    /**
     * Private constructor
     */
    private IntegerUtils(  )
    {
    }

    /**
     * Convert a given String into an integer.
     * <br />
     * If the String is blank or is not numerical, then it returns {@link #DEFAULT}.
     * @param strToParse the String to convert
     * @return the numerical value of the String
     */
    public static int convert( String strToParse )
    {
        return convert( strToParse, DEFAULT );
    }

    /**
     * Convert a given String into an integer.
     * <br />
     * If the String is blank or is not numerical, then it returns nDefault
     * @param strToParse the String to parse
     * @param nDefault the default value in case the conversion fails
     * @return the numerical value of the String
     */
    public static int convert( String strToParse, int nDefault )
    {
        if ( StringUtils.isNotBlank( strToParse ) && StringUtils.isNumeric( strToParse ) )
        {
            return Integer.parseInt( strToParse );
        }

        return nDefault;
    }

    /**
     * Check if the given String is numeric
     * @param strToCheck the String to check
     * @return true if it is numeric, false otherwise
     */
    public static boolean isNumeric( String strToCheck )
    {
        return StringUtils.isNotBlank( strToCheck ) && StringUtils.isNumeric( strToCheck );
    }

    /**
     * Check if the given String is not numerical
     * @param strToCheck the String to check
     * @return true if it is not numeric, false otherwise
     */
    public static boolean isNotNumeric( String strToCheck )
    {
        return !isNumeric( strToCheck );
    }

    /**
     * Check if the given number is equal to {@link #DEFAULT}
     * @param nNumber the number to check
     * @return true if it is equal, false otherwise
     */
    public static boolean isDefault( int nNumber )
    {
        return nNumber == DEFAULT;
    }
}
