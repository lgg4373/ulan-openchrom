/*******************************************************************************
 * Copyright (c) 2016 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.chromulan.system.control.manager.devices;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.inject.Singleton;

import org.chromulan.system.control.device.IControlDevices;
import org.chromulan.system.control.device.IDevicesProfiles;
import org.chromulan.system.control.device.setting.IDeviceSetting;
import org.chromulan.system.control.manager.devices.supplier.DeviceSupplier;
import org.chromulan.system.control.manager.events.IDataSupplierEvents;
import org.chromulan.system.control.preferences.PreferenceSupplier;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IPath;
import org.eclipse.e4.core.di.annotations.Creatable;
import org.eclipse.e4.core.services.events.IEventBroker;

@Creatable
@Singleton
public class DataSupplier {

	private DataStore dataStore;
	@Inject
	private IEventBroker eventBroker;
	@Inject
	private IExtensionRegistry registry;
	
	private final String FILE_NAME = "dataStore";

	
	public DataSupplier() {
		dataStore = new DataStore();
	}
	
	
	@PostConstruct
	public void postConstruct() {
		IConfigurationElement[] elements = registry.getConfigurationElementsFor(DeviceSupplier.ID);
		File file = getFile();
		
		if(file != null && file.exists()) {
			try {
				loadData(file,elements);
			} catch(ClassNotFoundException | IOException | CoreException e) {
				// logger.warn(e);
			}
		}
	}

	public IControlDevices getControlDevices() {

		if(dataStore != null) {
			return dataStore.getControlDevices();
		} else {
			return null;
		}
	}
	
	public List<IDeviceSetting> getDeviceSettings(){
		if(dataStore !=null){
			return dataStore.getDeviceSettings();
		}
		else {
			return null;
		}
	}

	public IDevicesProfiles getDevicesProfiles() {

		if(dataStore != null) {
			return dataStore.getDevicesProfiles();
		} else {
			return null;
		}
	}

	private File getFile() {

		IPath defPath = PreferenceSupplier.INSTANCE().getScopeContext().getLocation();
		File defFile = null;
		if(defPath != null) {
			defFile = defPath.append(FILE_NAME).toFile();
		} else {
			String path = PreferenceSupplier.INSTANCE().getPreferences().get("file", null);
			if(path != null) {
				defFile = new File(path, FILE_NAME);
			}
		}
		return defFile;
	}

	private void loadData(File file,IConfigurationElement[] elements) throws IOException, ClassNotFoundException, CoreException {

		FileInputStream inputStream = new FileInputStream(file);
		ObjectInputStream in = new ObjectInputStream(inputStream);
		dataStore.readExternal(in, elements);
		in.close();
		inputStream.close();
	}

	@PreDestroy
	public void save() {

		try {
			File file = getFile();
			if(file != null) {
				saveData(file);
			}
		} catch(IOException e) {
		}
	}

	private void saveData(File file) throws IOException {

		FileOutputStream outputStream = new FileOutputStream(file);
		ObjectOutputStream out = new ObjectOutputStream(outputStream);
		dataStore.writeExternal(out);
		out.close();
		outputStream.close();
	}


	public void updateControlDevices() {

		eventBroker.post(IDataSupplierEvents.TOPIC_DATA_UPDATE_DEVICES, this);
	}

	public void updateProfiles() {

		eventBroker.post(IDataSupplierEvents.TOPIC_DATA_UPDATE_PROFILES, this);
	}
	public void updateSettings() {

		eventBroker.post(IDataSupplierEvents.TOPIC_DATA_UPDATE_SETTINGS, this);
	}
}