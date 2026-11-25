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

import org.imgscalr.Scalr;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;


/**
 * Image utils class
 */
public final class ImageUtils
{
    /**
     * Private constructor
     */
    private ImageUtils(  )
    {
    }

    /**
     * Resize an image to a given width, keeping its proportions and its format: a PNG stays a PNG (with its transparency), a JPEG stays a JPEG,
     * so the content type stored with the image remains right
     * @param byteArray the original image
     * @param width the new width
     * @return the resized image, in the format of the original image
     * @throws IOException if the image cannot be read, or cannot be written back in its format
     */
    public static byte[] resizeImage( byte[] byteArray, int width )
        throws IOException
    {
        String strFormat;
        BufferedImage image;

        try ( ImageInputStream in = ImageIO.createImageInputStream( new ByteArrayInputStream( byteArray ) ) )
        {
            Iterator<ImageReader> readers = ( in == null ) ? null : ImageIO.getImageReaders( in );

            if ( ( readers == null ) || !readers.hasNext(  ) )
            {
                throw new IOException( "Unsupported image format" );
            }

            ImageReader reader = readers.next(  );

            try
            {
                reader.setInput( in );
                strFormat = reader.getFormatName(  );
                image = reader.read( 0 );
            }
            finally
            {
                reader.dispose(  );
            }
        }

        BufferedImage resizedImage = Scalr.resize( image, Scalr.Mode.FIT_TO_WIDTH, width );
        ByteArrayOutputStream out = new ByteArrayOutputStream(  );

        // ImageIO.write returns false, and writes nothing, when no writer accepts the image (a JPEG writer refuses an image with transparency)
        if ( !ImageIO.write( resizedImage, strFormat, out ) || ( out.size(  ) == 0 ) )
        {
            throw new IOException( "The resized image cannot be written in the " + strFormat + " format" );
        }

        return out.toByteArray(  );
    }
}
