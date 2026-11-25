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
package fr.paris.lutece.plugins.document.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import org.junit.jupiter.api.Test;

/**
 * Tests of the image resizing
 */
public class ImageUtilsTest
{
    @Test
    public void testResizePngWithTransparencyKeepsPng( ) throws IOException
    {
        byte [ ] resized = ImageUtils.resizeImage( image( BufferedImage.TYPE_INT_ARGB, "png" ), 50 );

        assertEquals( "png", format( resized ) );
        BufferedImage image = ImageIO.read( new ByteArrayInputStream( resized ) );
        assertEquals( 50, image.getWidth( ) );
        assertEquals( 25, image.getHeight( ) );
        assertEquals( true, image.getColorModel( ).hasAlpha( ) );
    }

    @Test
    public void testResizeJpegKeepsJpeg( ) throws IOException
    {
        byte [ ] resized = ImageUtils.resizeImage( image( BufferedImage.TYPE_INT_RGB, "jpg" ), 50 );

        assertEquals( "jpeg", format( resized ) );
        assertEquals( 50, ImageIO.read( new ByteArrayInputStream( resized ) ).getWidth( ) );
    }

    @Test
    public void testResizeNotAnImageFails( )
    {
        assertThrows( IOException.class, ( ) -> ImageUtils.resizeImage( "not an image".getBytes( ), 50 ) );
    }

    private static byte [ ] image( int nType, String strFormat ) throws IOException
    {
        ByteArrayOutputStream out = new ByteArrayOutputStream( );
        ImageIO.write( new BufferedImage( 100, 50, nType ), strFormat, out );

        return out.toByteArray( );
    }

    private static String format( byte [ ] bytes ) throws IOException
    {
        try ( ImageInputStream in = ImageIO.createImageInputStream( new ByteArrayInputStream( bytes ) ) )
        {
            Iterator<ImageReader> readers = ImageIO.getImageReaders( in );

            return readers.next( ).getFormatName( ).toLowerCase( );
        }
    }
}
