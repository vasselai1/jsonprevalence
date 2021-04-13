package jsonprevayler.search.matchers;

import java.io.IOException;
import java.io.InputStream;

import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintImageOptions;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;

/**
 * https://sourceafis.machinezoo.com/java
 */
public class FingerPrintScoreCalculator {

	public static double getScore(byte[] probeImageBytes, byte[] candidateImageBytes, double dpi) {
		FingerprintImageOptions opt = new FingerprintImageOptions().dpi(dpi);
		FingerprintTemplate probe = new FingerprintTemplate(new FingerprintImage(probeImageBytes, opt));
		FingerprintTemplate candidate = new FingerprintTemplate(new FingerprintImage(candidateImageBytes, opt));
		return new FingerprintMatcher(probe).match(candidate);
	}
	
	public static double getScore(InputStream probeImageStream, InputStream candidateImageStream, double dpi) throws IOException {
		return getScore(probeImageStream.readAllBytes(), candidateImageStream.readAllBytes(), dpi);
	}
	
}