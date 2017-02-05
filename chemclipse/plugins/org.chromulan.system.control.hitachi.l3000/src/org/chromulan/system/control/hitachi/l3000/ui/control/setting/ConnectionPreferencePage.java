/*******************************************************************************
 * Copyright (c) 2016, 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.hitachi.l3000.ui.control.setting;

import java.util.Enumeration;

import org.chromulan.system.control.hitachi.l3000.model.ControlDevice;
import org.chromulan.system.control.hitachi.l3000.model.DeviceInterface;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import purejavacomm.CommPortIdentifier;

public class ConnectionPreferencePage extends PreferencePage {

	private IObservableValue<String> boudRate;
	private ControlDevice controlDevice;
	private IObservableValue<Boolean> controlSignal;
	private DataBindingContext dbc;
	private IObservableValue<String> delimiter;
	private String delimiterCR = "CR";
	private String delimiterCRLF = "CR+LF";
	private DeviceInterface deviceInterface;
	private IObservableValue<String> name;
	private IObservableValue<Boolean> parity;

	public ConnectionPreferencePage(DeviceInterface deviceInterface) {
		super("Connection");
		this.controlDevice = deviceInterface.getControlDevice();
		this.deviceInterface = deviceInterface;
		this.name = new WritableValue<>(controlDevice.getPortName(), String.class);
		this.boudRate = new WritableValue<>(Integer.toString(controlDevice.getPortBaudRate()), String.class);
		if(controlDevice.getPortDelimeter().equals(ControlDevice.DELIMITER_CR)) {
			this.delimiter = new WritableValue<>(delimiterCR, String.class);
		} else {
			this.delimiter = new WritableValue<>(delimiterCRLF, String.class);
		}
		this.parity = new WritableValue<>(controlDevice.isPortEventParity(), Boolean.class);
		this.controlSignal = new WritableValue<>(controlDevice.isPortDataControlSignal(), Boolean.class);
		this.dbc = new DataBindingContext();
	}

	@Override
	protected Control createContents(Composite composite) {

		Composite parent = new Composite(composite, SWT.None);
		Label label = new Label(parent, SWT.None);
		label.setText("Select Port Name");
		Combo combo = getNames(parent);
		dbc.bindValue(WidgetProperties.selection().observe(combo), name);
		label = new Label(parent, SWT.None);
		label.setText("Select Boud rate");
		combo = getBaudRate(parent);
		dbc.bindValue(WidgetProperties.selection().observe(combo), boudRate);
		label = new Label(parent, SWT.None);
		label.setText("Select Delimiter");
		combo = getDelimiter(parent);
		dbc.bindValue(WidgetProperties.selection().observe(combo), delimiter);
		label = new Label(parent, SWT.None);
		label.setText("Parity even");
		Button button = new Button(parent, SWT.CHECK);
		dbc.bindValue(WidgetProperties.selection().observe(button), parity);
		label = new Label(parent, SWT.None);
		label.setText("Data control signal");
		button = new Button(parent, SWT.CHECK);
		dbc.bindValue(WidgetProperties.selection().observe(button), controlSignal);
		GridLayoutFactory.swtDefaults().numColumns(2).generateLayout(parent);
		return composite;
	}

	private Combo getBaudRate(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.add(Integer.toString(ControlDevice.BOUD_RATE_4800));
		combo.add(Integer.toString(ControlDevice.BOUD_RATE_2400));
		combo.add(Integer.toString(ControlDevice.BOUD_RATE_1200));
		combo.add(Integer.toString(ControlDevice.BOUD_RATE_600));
		combo.add(Integer.toString(ControlDevice.BOUD_RATE_300));
		combo.add(Integer.toString(ControlDevice.BOUD_RATE_150));
		combo.add(Integer.toString(ControlDevice.BOUD_RATE_75));
		return combo;
	}

	private Combo getDelimiter(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		combo.add(delimiterCR);
		combo.add(delimiterCRLF);
		return combo;
	}

	private Combo getNames(Composite parent) {

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		Enumeration<CommPortIdentifier> enumerations = CommPortIdentifier.getPortIdentifiers();
		while(enumerations.hasMoreElements()) {
			CommPortIdentifier commPortIdentifier = enumerations.nextElement();
			String name = commPortIdentifier.getName();
			int portType = commPortIdentifier.getPortType();
			if(portType == CommPortIdentifier.PORT_SERIAL) {
				combo.add(name);
			}
		}
		return combo;
	}

	@Override
	protected void performDefaults() {

		dbc.updateTargets();
	}

	@Override
	public boolean performOk() {

		String name = this.name.getValue();
		if(name == null || name.isEmpty()) {
			return false;
		}
		int boudRate = Integer.valueOf(this.boudRate.getValue());
		boolean parity = this.parity.getValue();
		boolean controlSignal = this.controlSignal.getValue();
		String delimiter = null;
		if(this.delimiter.getValue().equals(delimiterCR)) {
			delimiter = ControlDevice.DELIMITER_CR;
		} else {
			delimiter = ControlDevice.DELIMITER_CRLF;
		}
		if(!controlDevice.isConnected()) {
			return deviceInterface.openConnection(name, boudRate, parity, controlSignal, delimiter, true);
		} else {
			if(controlDevice.getPortName().equals(name)) {
				return deviceInterface.setParameters(boudRate, parity, controlSignal, delimiter);
			} else {
				deviceInterface.closeConnection();
				return deviceInterface.openConnection(name, boudRate, parity, controlSignal, delimiter, true);
			}
		}
	}
}
