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
package net.abi.abisEngine.util.exceptions;

public class AERuntimeException extends RuntimeException {
	private static final long serialVersionUID = 666666555666666L;

	public AERuntimeException(Throwable cause) {
		super(cause);
	}

	public AERuntimeException(String cause, Throwable t) {
		super(cause, t);
	}

	public AERuntimeException(String cause) {
		super(cause);
	}

	public AERuntimeException(String cause, Exception e) {
		super(cause, e);
	}

}
