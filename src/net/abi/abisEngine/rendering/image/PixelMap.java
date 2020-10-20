/*******************************************************************************
 * Copyright 2020 Abinash Singh | ABI INC.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package net.abi.abisEngine.rendering.image;

import java.nio.ByteBuffer;

import net.abi.abisEngine.rendering.asset.AssetI;
import net.abi.abisEngine.util.Color;

public class PixelMap implements AssetI {

	private ByteBuffer image;
	private ImageMetaData md;
	private int refs;

	/**
	 * ./res/textures/ is automatically prepended.
	 * 
	 * @param file
	 */
	public PixelMap(ByteBuffer image, ImageMetaData md) {
		this.image = image;
		this.md = md;
		this.refs = 1;
	}

	public ByteBuffer getPixelsInByteBuffer() {
		return image;
	}
	
	public void setPixel(int x, int y, Color c) {
		int offset = (md.width * y + x) * 4;
		image.flip();
		image.put(offset + 0, (byte) c.getRed());
		image.put(offset + 1, (byte) c.getRed());
		image.put(offset + 2, (byte) c.getRed());
		image.put(offset + 3, (byte) c.getRed());
		image.flip();
	}

	public Color getPixel(int x, int y) {
		int offset = (md.width * y + x) * 4;
		int r = image.get(offset + 0);
		int g = image.get(offset + 1);
		int b = image.get(offset + 2);
		int a = image.get(offset + 3);
		return new Color(r, g, b, a);
	}
	
	public void setData(ByteBuffer data) {
		this.image = data;
	}

	public ImageMetaData getImageMetaData() {
		return md;
	}

	@Override
	public void dispose() {
		if (refs <= 0) {
			image = null;
		} else {
			decRef();
		}
	}

	@Override
	public void incRef() {
		refs += 1;
	}

	@Override
	public int incAndGetRef() {
		incRef();
		return refs;
	}

	@Override
	public void decRef() {
		this.refs -= 1;
		if (refs <= 0) {
			dispose();
		}
	}

	@Override
	public int decAndGetRef() {
		decRef();
		return refs;
	}

	@Override
	public int getRefs() {
		return refs;
	}

}
