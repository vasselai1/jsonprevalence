package br.tec.jsonprevayler.searchfilter.matchers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.codec.language.DoubleMetaphone;

public class PhoneticComparator {

	private static final DoubleMetaphone DOUBLE_METAPHONE = new DoubleMetaphone();
	
	public static boolean isEquals(String text1, String text2) {
		return isEquals(text1, text2, null);
	}
	
	public static boolean isEquals(String text1, String text2, List<String[]> synonyns) {
		if ((synonyns == null) || (synonyns.isEmpty())) {
			return DOUBLE_METAPHONE.isDoubleMetaphoneEqual(text1, text2);
		}
		if (DOUBLE_METAPHONE.isDoubleMetaphoneEqual(text1, text2)) {
			return true;
		}
		return (countEquals(text1, text2, synonyns) > 0);
	}	
	
	public static int countEquals(String text1, String text2, List<String[]> synonyns) {
		List<String> text1Splited = Arrays.asList(text1.split(" "));
		List<String> text2Splited = Arrays.asList(text2.split(" "));
		int retorno = countInternEquals(text1Splited, text2Splited);
		if ((synonyns == null) || (synonyns.isEmpty())) {
			return retorno;
		}
		List<String> synonynsList1 = getSynonyns(text1Splited, synonyns);
		List<String> synonynsList2 = getSynonyns(text2Splited, synonyns);
		return (retorno + countInternEquals(synonynsList1, synonynsList2));
	}
	
	public static int countEquals(String text1, String text2) {
		return countEquals(text1, text2, null);
	}	
	
	private static int countInternEquals(List<String> words1, List<String> words2) {
		int retorno = 0;
		for (String word1 : words1) {
			for (String word2 : words2){
				if (isEquals(word1, word2)) {
					retorno++;
				}
			}			
		}
		return retorno;
	}
	
	private static List<String> getSynonyns(List<String> text1Splited, List<String[]> synonyns) {
		List<String> retorno = new ArrayList<String>();
		for (String word : text1Splited) {
			for (String[] arraySynonyns : synonyns) {
				for (String synonymWord : arraySynonyns) {
					if (word.trim().equalsIgnoreCase(synonymWord.trim())) {
						retorno.addAll(Arrays.asList(arraySynonyns));
						break;
					}
				}
				for (String synonymWord : arraySynonyns) {
					if (isEquals(word, synonymWord)) {
						retorno.addAll(Arrays.asList(arraySynonyns));
						break;
					}
				}
			}
			
		}
		return retorno;
	}
	
}