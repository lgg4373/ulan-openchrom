/*******************************************************************************
 * Copyright (c) 2016 PC.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * PC - initial API and implementation
*******************************************************************************/
package org.chromulan.system.control.model;

import java.util.ArrayList;
import java.util.List;

import org.chromulan.system.control.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.csd.model.core.IChromatogramCSD;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.exceptions.ChromatogramIsNullException;
import org.eclipse.chemclipse.swt.ui.converter.SeriesConverter;
import org.eclipse.chemclipse.swt.ui.series.IMultipleSeries;
import org.eclipse.chemclipse.swt.ui.series.ISeries;
import org.eclipse.chemclipse.swt.ui.support.Colors;
import org.eclipse.chemclipse.swt.ui.support.IColorScheme;
import org.eclipse.chemclipse.swt.ui.support.Sign;
import org.eclipse.chemclipse.wsd.model.core.AbstractChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IChromatogramWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.wsd.model.core.IScanWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.ChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.selection.IChromatogramSelectionWSD;
import org.eclipse.chemclipse.wsd.model.core.support.IMarkedWavelengths;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelength;
import org.eclipse.chemclipse.wsd.model.core.support.MarkedWavelengths;
import org.swtchart.ILineSeries;
import org.swtchart.LineStyle;
import org.swtchart.ILineSeries.PlotSymbolType;
import org.swtchart.ISeries.SeriesType;

public class ChromatogramWSDAcquisition extends AbstractChromatogramAcquisition implements IChromatogramWSDAcquisition {
	
	IMarkedWavelengths selectedMarkedWavelengths;

	public ChromatogramWSDAcquisition(int interval,int delay) {
		super(interval, delay);
		selectedMarkedWavelengths = new MarkedWavelengths();
	}

	@Override
	public IMultipleSeries getSeries() {
		IMultipleSeries multipleSeries = null;
		synchronized(this) {
			IChromatogramWSD chromatogramWSD = geChromatogramWSD();
			List<Integer> wavelengths = new ArrayList<Integer>(selectedMarkedWavelengths.getWavelengths());
			try {
				ChromatogramSelectionWSD chromatogramSelection = new ChromatogramSelectionWSD(chromatogramWSD, false);
				multipleSeries = SeriesConverterWSD.convertChromatogram(chromatogramSelection, wavelengths, false, Sign.POSITIVE);
			} catch(ChromatogramIsNullException e) {
			}
		}
		if(multipleSeries != null)
		{
			int size = multipleSeries.getMultipleSeries().size();
			ISeries series;
			String colorSchemeOverlay = PreferenceSupplier.getColorSchemeOverlay();
			IColorScheme colorScheme = Colors.getColorScheme(colorSchemeOverlay);
			/*
			 * Set the series.
			 */
			for(int i = 0; i < size; i++) {
				series = multipleSeries.getMultipleSeries().get(i);
				setAdditionalIonSeries(series, colorScheme.getColor());
				colorScheme.incrementColor();
			}	
		}
		
	}



	@Override
	protected IChromatogram createChromatogram() {

		IChromatogramWSD chromatogramWSD = new AbstractChromatogramWSD() {
			
			@Override
			public double getPeakIntegratedArea() {
								
				return 0;
			}
		};
		return chromatogramWSD;
	}

	@Override
	public IChromatogramWSD geChromatogramWSD() {
		IChromatogram chromatogram = getChromatogram();
		if(chromatogram instanceof IChromatogramWSD) {
			return  (IChromatogramWSD)chromatogram;		
		}	
		return null;
	}
	
	public IMarkedWavelengths getWaveLenaght()
	{
		IMarkedWavelengths wavelengths = new MarkedWavelengths();
		synchronized(this) {
			IChromatogramWSD wsdChromatogram = geChromatogramWSD();
			IScanWSD scan = (IScanWSD)wsdChromatogram.getScans().stream().findFirst().get();
			for(IScanSignalWSD signal : scan.getScanSignals()) {
				wavelengths.add(new MarkedWavelength(signal.getWavelength()));
			}
			return wavelengths;
		}
	}
	
	public IMarkedWavelengths getSelectedWaveLenaght(){
		
		return selectedMarkedWavelengths;
	}


	
}
