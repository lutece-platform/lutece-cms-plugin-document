/*
 * Copyright (c) 2002-2025, City of Paris
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
package fr.paris.lutece.plugins.document.service.search;

import fr.paris.lutece.plugins.priority.annotation.LutecePriority;
import fr.paris.lutece.portal.service.parser.Parser;
import fr.paris.lutece.portal.service.parser.ParserException;
import fr.paris.lutece.portal.service.util.AppLogService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.nio.charset.StandardCharsets;

/**
 *
 * HTML Parser
 *
 */
@ApplicationScoped
@Alternative
@Named( "parser.defaultParser" )
@LutecePriority( "2" )
public class DefaultDocumentIndexerParser implements Parser
{

	private static final String DEFAULT_CHARSET = StandardCharsets.UTF_8.name( );

	/**
	 * Parse the input stream content using Jsoup to extract text from HTML.
	 * This method reads the HTML content and extracts only the text,
	 * removing all HTML tags and scripts.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public String parseToString( InputStream is ) throws ParserException
	{
		try
		{

			Document doc = Jsoup.parse( is, DEFAULT_CHARSET, "" );

			// extract text and erase html tag
			String text = doc.text( );

			return text;

		}
		catch( IOException e )
		{
			AppLogService.error( e.getMessage( ), e );
			throw new ParserException( "Erreur parsing HTML: " + e.getMessage( ), e );

		}
	}

	/**
	 * Parse the input stream and return a Reader containing the extracted text.
	 * This method uses parseToString internally and wraps the result in a
	 * StringReader.
	 *
	 * {@inheritDoc}
	 */
	@Override
	public Reader parse( InputStream is ) throws ParserException
	{
		String content = parseToString( is );
		return new StringReader( content );
	}

}
