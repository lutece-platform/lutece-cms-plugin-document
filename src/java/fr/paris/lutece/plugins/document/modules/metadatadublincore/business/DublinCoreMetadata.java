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
package fr.paris.lutece.plugins.document.modules.metadatadublincore.business;

import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.xml.XmlUtil;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.xmlrules.DigesterLoader;

import org.xml.sax.SAXException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;

import java.net.URL;


/**
 * This class represents the business object DublinCoreMetadata
 */
public class DublinCoreMetadata
{
    private static final String PROPERTY_DEFAULT_TITLE = "document-metadatadublincore.default.title";
    private static final String PROPERTY_DEFAULT_CREATOR = "document-metadatadublincore.default.creator";
    private static final String PROPERTY_DEFAULT_SUBJECT = "document-metadatadublincore.default.subject";
    private static final String PROPERTY_DEFAULT_DESCRIPTION = "document-metadatadublincore.default.description";
    private static final String PROPERTY_DEFAULT_PUBLISHER = "document-metadatadublincore.default.publisher";
    private static final String PROPERTY_DEFAULT_CONTRIBUTOR = "document-metadatadublincore.default.contributor";
    private static final String PROPERTY_DEFAULT_DATE = "document-metadatadublincore.default.date";
    private static final String PROPERTY_DEFAULT_TYPE = "document-metadatadublincore.default.type";
    private static final String PROPERTY_DEFAULT_FORMAT = "document-metadatadublincore.default.format";
    private static final String PROPERTY_DEFAULT_IDENTIFIER = "document-metadatadublincore.default.identifier";
    private static final String PROPERTY_DEFAULT_SOURCE = "document-metadatadublincore.default.source";
    private static final String PROPERTY_DEFAULT_LANGUAGE = "document-metadatadublincore.default.language";
    private static final String PROPERTY_DEFAULT_RELATION = "document-metadatadublincore.default.relation";
    private static final String PROPERTY_DEFAULT_COVERAGE = "document-metadatadublincore.default.coverage";
    private static final String PROPERTY_DEFAULT_RIGTHS = "document-metadatadublincore.default.rigths";
    private static final String FILE_RULES = "/fr/paris/lutece/plugins/document/modules/metadatadublincore/business/dublincore-digester-rules.xml";

    // Variables declarations
    private String _strTitle;
    private String _strCreator;
    private String _strSubject;
    private String _strDescription;
    private String _strPublisher;
    private String _strContributor;
    private String _strDate;
    private String _strType;
    private String _strFormat;
    private String _strIdentifier;
    private String _strSource;
    private String _strLanguage;
    private String _strRelation;
    private String _strCoverage;
    private String _strRights;

    /**
     * Default constructor
     */
    public DublinCoreMetadata(  )
    {
        _strTitle = AppPropertiesService.getProperty( PROPERTY_DEFAULT_TITLE ) != null ? AppPropertiesService.getProperty( PROPERTY_DEFAULT_TITLE ) : "";
        _strCreator = AppPropertiesService.getProperty( PROPERTY_DEFAULT_CREATOR ) != null ? AppPropertiesService.getProperty( PROPERTY_DEFAULT_CREATOR ) : "";
        _strSubject = AppPropertiesService.getProperty( PROPERTY_DEFAULT_SUBJECT ) != null ? AppPropertiesService.getProperty( PROPERTY_DEFAULT_SUBJECT ) : "";
        _strDescription = AppPropertiesService.getProperty( PROPERTY_DEFAULT_DESCRIPTION ) != null ? AppPropertiesService.getProperty( PROPERTY_DEFAULT_DESCRIPTION ) : "";
        _strPublisher = AppPropertiesService.getProperty( PROPERTY_DEFAULT_PUBLISHER ) != null ? AppPropertiesService.getProperty( PROPERTY_DEFAULT_PUBLISHER ) : "";
        _strContributor = AppPropertiesService.getProperty( PROPERTY_DEFAULT_CONTRIBUTOR ) != null ? AppPropertiesService.getProperty( PROPERTY_DEFAULT_CONTRIBUTOR ) : "";
        _strDate = AppPropertiesService.getProperty( PROPERTY_DEFAULT_DATE ) != null ? AppPropertiesService.getProperty( PROPERTY_DEFAULT_DATE ) : "";
        _strType = AppPropertiesService.getProperty( PROPERTY_DEFAULT_TYPE ) != null ? AppPropertiesService.getProperty( PROPERTY_DEFAULT_TYPE ) : "";
        _strFormat = AppPropertiesService.getProperty( PROPERTY_DEFAULT_FORMAT ) != null ? AppPropertiesService.getProperty( PROPERTY_DEFAULT_FORMAT ) : "";
        _strIdentifier = AppPropertiesService.getProperty( PROPERTY_DEFAULT_IDENTIFIER ) != null ? AppPropertiesService.getProperty( PROPERTY_DEFAULT_IDENTIFIER ) : "";
        _strSource = AppPropertiesService.getProperty( PROPERTY_DEFAULT_SOURCE ) != null ? AppPropertiesService.getProperty( PROPERTY_DEFAULT_SOURCE ) : "";
        _strLanguage = AppPropertiesService.getProperty( PROPERTY_DEFAULT_LANGUAGE ) != null ? AppPropertiesService.getProperty( PROPERTY_DEFAULT_LANGUAGE ) : "";
        _strRelation = AppPropertiesService.getProperty( PROPERTY_DEFAULT_RELATION ) != null ? AppPropertiesService.getProperty( PROPERTY_DEFAULT_RELATION ) : "";
        _strCoverage = AppPropertiesService.getProperty( PROPERTY_DEFAULT_COVERAGE ) != null ? AppPropertiesService.getProperty( PROPERTY_DEFAULT_COVERAGE ) : "";
        _strRights = AppPropertiesService.getProperty( PROPERTY_DEFAULT_RIGTHS ) != null ? AppPropertiesService.getProperty( PROPERTY_DEFAULT_RIGTHS ) : "";
    }

    /**
     * Returns the Title
     * @return The Title
     */
    public String getTitle(  )
    {
        return _strTitle;
    }

    /**
     * Sets the Title
     * @param strTitle The Title
     */
    public void setTitle( String strTitle )
    {
        _strTitle = strTitle;
    }

    /**
     * Returns the Creator
     * @return The Creator
     */
    public String getCreator(  )
    {
        return _strCreator;
    }

    /**
     * Sets the Creator
     * @param strCreator The Creator
     */
    public void setCreator( String strCreator )
    {
        _strCreator = strCreator;
    }

    /**
     * Returns the Subject
     * @return The Subject
     */
    public String getSubject(  )
    {
        return _strSubject;
    }

    /**
     * Sets the Subject
     * @param strSubject The Subject
     */
    public void setSubject( String strSubject )
    {
        _strSubject = strSubject;
    }

    /**
     * Returns the Description
     * @return The Description
     */
    public String getDescription(  )
    {
        return _strDescription;
    }

    /**
     * Sets the Description
     * @param strDescription The Description
     */
    public void setDescription( String strDescription )
    {
        _strDescription = strDescription;
    }

    /**
     * Returns the Publisher
     * @return The Publisher
     */
    public String getPublisher(  )
    {
        return _strPublisher;
    }

    /**
     * Sets the Publisher
     * @param strPublisher The Publisher
     */
    public void setPublisher( String strPublisher )
    {
        _strPublisher = strPublisher;
    }

    /**
     * Returns the Contributor
     * @return The Contributor
     */
    public String getContributor(  )
    {
        return _strContributor;
    }

    /**
     * Sets the Contributor
     * @param strContributor The Contributor
     */
    public void setContributor( String strContributor )
    {
        _strContributor = strContributor;
    }

    /**
     * Returns the Date
     * @return The Date
     */
    public String getDate(  )
    {
        return _strDate;
    }

    /**
     * Sets the Date
     * @param strDate The Date
     */
    public void setDate( String strDate )
    {
        _strDate = strDate;
    }

    /**
     * Returns the Type
     * @return The Type
     */
    public String getType(  )
    {
        return _strType;
    }

    /**
     * Sets the Type
     * @param strType The Type
     */
    public void setType( String strType )
    {
        _strType = strType;
    }

    /**
     * Returns the Format
     * @return The Format
     */
    public String getFormat(  )
    {
        return _strFormat;
    }

    /**
     * Sets the Format
     * @param strFormat The Format
     */
    public void setFormat( String strFormat )
    {
        _strFormat = strFormat;
    }

    /**
     * Returns the Identifier
     * @return The Identifier
     */
    public String getIdentifier(  )
    {
        return _strIdentifier;
    }

    /**
     * Sets the Identifier
     * @param strIdentifier The Identifier
     */
    public void setIdentifier( String strIdentifier )
    {
        _strIdentifier = strIdentifier;
    }

    /**
     * Returns the Source
     * @return The Source
     */
    public String getSource(  )
    {
        return _strSource;
    }

    /**
     * Sets the Source
     * @param strSource The Source
     */
    public void setSource( String strSource )
    {
        _strSource = strSource;
    }

    /**
     * Returns the Language
     * @return The Language
     */
    public String getLanguage(  )
    {
        return _strLanguage;
    }

    /**
     * Sets the Language
     * @param strLanguage The Language
     */
    public void setLanguage( String strLanguage )
    {
        _strLanguage = strLanguage;
    }

    /**
     * Returns the Relation
     * @return The Relation
     */
    public String getRelation(  )
    {
        return _strRelation;
    }

    /**
     * Sets the Relation
     * @param strRelation The Relation
     */
    public void setRelation( String strRelation )
    {
        _strRelation = strRelation;
    }

    /**
     * Returns the Coverage
     * @return The Coverage
     */
    public String getCoverage(  )
    {
        return _strCoverage;
    }

    /**
     * Sets the Coverage
     * @param strCoverage The Coverage
     */
    public void setCoverage( String strCoverage )
    {
        _strCoverage = strCoverage;
    }

    /**
     * Returns the Rigths
     * @return The Rigths
     */
    public String getRights(  )
    {
        return _strRights;
    }

    /**
     * Sets the Rigths
     * @param strRights The Rigths
     */
    public void setRights( String strRights )
    {
        _strRights = strRights;
    }

    /**
     * Get the XML of the current dublin core metadata
     * @return The XML representing this dublin core metadata
     */
    public String getXml(  )
    {
        StringBuffer sbXml = new StringBuffer(  );
        XmlUtil.beginElement( sbXml, "metadata" );

        if ( ( _strTitle != null ) && ( !_strTitle.equals( "" ) ) )
        {
            XmlUtil.addElement( sbXml, "title", _strTitle );
        }

        if ( ( _strCreator != null ) && ( !_strCreator.equals( "" ) ) )
        {
            XmlUtil.addElement( sbXml, "creator", _strCreator );
        }

        if ( ( _strSubject != null ) && ( !_strSubject.equals( "" ) ) )
        {
            XmlUtil.addElement( sbXml, "subject", _strSubject );
        }

        if ( ( _strDescription != null ) && ( !_strDescription.equals( "" ) ) )
        {
            XmlUtil.addElement( sbXml, "description", _strDescription );
        }

        if ( ( _strPublisher != null ) && ( !_strPublisher.equals( "" ) ) )
        {
            XmlUtil.addElement( sbXml, "publisher", _strPublisher );
        }

        if ( ( _strContributor != null ) && ( !_strContributor.equals( "" ) ) )
        {
            XmlUtil.addElement( sbXml, "contributor", _strContributor );
        }

        if ( ( _strDate != null ) && ( !_strDate.equals( "" ) ) )
        {
            XmlUtil.addElement( sbXml, "date", _strDate );
        }

        if ( ( _strType != null ) && ( !_strType.equals( "" ) ) )
        {
            XmlUtil.addElement( sbXml, "type", _strType );
        }

        if ( ( _strFormat != null ) && ( !_strFormat.equals( "" ) ) )
        {
            XmlUtil.addElement( sbXml, "format", _strFormat );
        }

        if ( ( _strIdentifier != null ) && ( !_strIdentifier.equals( "" ) ) )
        {
            XmlUtil.addElement( sbXml, "identifier", _strIdentifier );
        }

        if ( ( _strSource != null ) && ( !_strSource.equals( "" ) ) )
        {
            XmlUtil.addElement( sbXml, "source", _strSource );
        }

        if ( ( _strLanguage != null ) && ( !_strLanguage.equals( "" ) ) )
        {
            XmlUtil.addElement( sbXml, "language", _strLanguage );
        }

        if ( ( _strRelation != null ) && ( !_strRelation.equals( "" ) ) )
        {
            XmlUtil.addElement( sbXml, "relation", _strRelation );
        }

        if ( ( _strContributor != null ) && ( !_strContributor.equals( "" ) ) )
        {
            XmlUtil.addElement( sbXml, "coverage", _strCoverage );
        }

        if ( ( _strRights != null ) && ( !_strRights.equals( "" ) ) )
        {
            XmlUtil.addElement( sbXml, "rights", _strRights );
        }

        XmlUtil.endElement( sbXml, "metadata" );

        return sbXml.toString(  );
    }

    /**
     * Load dublin core metadata from an XML stream
     * @param strXmlData The XML content to get the attributes from
     */
    public void load( String strXmlData )
    {
        // Configure Digester from XML ruleset
        URL rules = getClass(  ).getResource( FILE_RULES );
        Digester digester = DigesterLoader.createDigester( rules );

        // Push empty List onto Digester's Stack
        digester.push( this );

        try
        {
            if ( strXmlData != null )
            {
                StringReader sr = new StringReader( strXmlData );
                digester.parse( sr );
            }
        }
        catch ( FileNotFoundException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( SAXException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
        catch ( IOException e )
        {
            AppLogService.error( e.getMessage(  ), e );
        }
    }
}
