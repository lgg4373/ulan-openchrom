/*******************************************************************************
 * Copyright (c) 2015 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Jan Holy - initial API and implementation
*******************************************************************************/
package org.chromulan.system.control.ui.analysis.support;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;


public class ValidatorName implements IValidator{

	@Override
	public IStatus validate(Object value) {
		
		if(value == null)
		{
			return ValidationStatus.error("NAME: name is empty");
		}
		if(value instanceof String)
		{
			
			String ss = (String)value;
			if(ss.isEmpty())
			{
				return ValidationStatus.error("NAME: name is empty");
			}else if (ss.contains("<"))
			{
				return ValidationStatus.error("NAME: name contains unsupported character <");
			}else if (ss.contains(">"))
			{
				return ValidationStatus.error("NAME: name contains unsupported character >");
			}else if (ss.contains(":"))
			{
				return ValidationStatus.error("NAME: name contains unsupported character :");
			}else if (ss.contains("/"))
			{
				return ValidationStatus.error("NAME: name contains unsupported character /");
			}else if (ss.contains("\\"))
			{
				return ValidationStatus.error("NAME: name contains unsupported character \\");
			}else if (ss.contains("|"))
			{
				return ValidationStatus.error("NAME: name contains unsupported character |");
			}else if (ss.contains("?"))
			{
				return ValidationStatus.error("NAME: name contains unsupported character ?");
			}else if (ss.contains("*"))
			{
				return ValidationStatus.error("NAME: name contains unsupported character *");
			}else
			{
				return ValidationStatus.OK_STATUS;
			}
			
		}else
		{
			return ValidationStatus.error("NAME: invalid name");
		}
		
		
		
	}
}