/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.eomodeler.core.kvc;

import java.util.Comparator;

public class KVCComparator implements Comparator {
	private KeyPath myKeyPath;

	public KVCComparator(Class _class, String _keyPath) {
		myKeyPath = new ResolvedKeyPath(_class, _keyPath);
	}

	public KVCComparator(String _keyPath) {
		myKeyPath = new KeyPath(_keyPath);
	}

	public int compare(Object _o1, Object _o2) {
		try {
			Object value1 = myKeyPath.getValue(_o1);
			Object value2 = myKeyPath.getValue(_o2);
			int results;
			if (value1 == null) {
				if (value2 == null) {
					results = 0;
				} else if (value2 instanceof Comparable) {
					results = 1;
				} else {
					throw new IllegalArgumentException(myKeyPath + " did not return a comparable value from " + _o2);
				}
			} else if (value2 == null) {
				results = -1;
			} else if (value1 instanceof Comparable) {
				results = ((Comparable) value1).compareTo(value2);
			} else {
				throw new IllegalArgumentException(myKeyPath + " did not return a comparable value from " + _o1);
			}
			return results;
		} catch (Throwable t) {
			throw new RuntimeException("Failed to retrieve value of " + myKeyPath + " on " + _o1 + " or " + _o2 + ".", t);
		}
	}

}