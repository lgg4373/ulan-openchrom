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
package org.chromulan.system.control.model.chromatogram;

import java.util.List;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;

public class ChromatogramRecording implements IChromatogramRecording {

	private IChromatogram chromatogram;

	public ChromatogramRecording() {
	}

	public void addScan(IScan scan) {

		synchronized(chromatogram) {
			chromatogram.addScan(scan);
		}
	}

	public void addScans(List<IScan> scans) {

		synchronized(chromatogram) {
			chromatogram.getScans().addAll(scans);
		}
	}

	public void setScanInterval(int milliseconds) {

		synchronized(chromatogram) {
			if(milliseconds != chromatogram.getScanInterval()) {
				reset();
				chromatogram.setScanInterval(milliseconds);
			}
		}
	}

	public int getScanInterval() {

		synchronized(chromatogram) {
			return chromatogram.getScanInterval();
		}
		
	}

	public void setScanDelay(int milliseconds) {

		synchronized(chromatogram) {
			if(chromatogram.getScanDelay() != milliseconds) {
				chromatogram.setScanDelay(milliseconds);
				chromatogram.recalculateRetentionTimes();
			}
		}
	}

	public void resetRecording() {

		synchronized(chromatogram) {
			reset();
		}
	}

	private void reset() {

		chromatogram.removeScans(1, chromatogram.getNumberOfScans());
	}

	public IChromatogram getChromatogram() {

		synchronized(chromatogram) {
			return chromatogram;
		}
		
	}
	
	public void setChromatogram(IChromatogram chromatogram)
	{
		synchronized(chromatogram) {
			this.chromatogram = chromatogram;
		}
		
	}
}