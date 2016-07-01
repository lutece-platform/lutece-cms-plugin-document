/*
 * Copyright (c) 2002-2014, Mairie de Paris
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

import javax.imageio.ImageIO;


/**
 * Image utils class
 */
public final class ImageUtils
{
    /** Parameter JPG */
    private static final String PARAMETER_JPG = "jpg";

    /**
     * Private constructor
     */
    private ImageUtils(  )
    {
    }

    /**
     * Resize an image
     * @param byteArray the original byte arrau
     * @param width the new width
     * @return the resize byte array
     * @throws IOException occurs during treatment
     */
    public static byte[] resizeImage( byte[] byteArray, int width )
        throws IOException
    {
        // Crop image if needed
        ByteArrayInputStream in = new ByteArrayInputStream( byteArray );
        ByteArrayOutputStream out = new ByteArrayOutputStream(  );
        BufferedImage image = ImageIO.read( in );
        BufferedImage resizedImage;
        resizedImage = Scalr.resize( image, Scalr.Mode.FIT_TO_WIDTH, width );
        ImageIO.write( resizedImage, PARAMETER_JPG, out );

        return out.toByteArray(  );
    }
}
