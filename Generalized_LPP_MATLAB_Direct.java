import java.io.File;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;

import java.util.ArrayList;

import simplenlg.framework.*;
import simplenlg.lexicon.*;
import simplenlg.realiser.english.*;
import simplenlg.phrasespec.*;
import simplenlg.features.*;

import java.util.List;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Math;

import com.mathworks.engine.*;

import com.jmatio.io.*;
import com.jmatio.types.*;

import java.util.regex.*;

public class Generalized_LPP_MATLAB_Direct {

	public static void main(String[] args) {

		try {
			ArrayList<String> Name_list = new ArrayList<String>();
			ArrayList<ArrayList<Integer>> value_of_Variables_list = new ArrayList<ArrayList<Integer>>();
			ArrayList<Integer> ind_values_Variables = new ArrayList<Integer>();
			ArrayList<String> Type_list = new ArrayList<String>();
			ArrayList<Integer> RHS_list = new ArrayList<Integer>();
			ArrayList<String> name_of_Variables_list = new ArrayList<String>();

			String name_of_Authority = null;
			int num_of_Variables = 0;
			int num_of_Constraints = 0;
			String OFVerb = null;
			String VariableVerb = null;
			String var = "Variable";

			File inputFile = new File("E:\\Spain_2018\\SDC_Work\\Problems_LPP\\WHSC.txt");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("Metadata");

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					// System.out.println(
					// "Authority : " +
					// eElement.getElementsByTagName("Authority").item(0).getTextContent());
					name_of_Authority = eElement.getElementsByTagName("Authority").item(0).getTextContent();

					// System.out.println(
					// "Variables : " +
					// eElement.getElementsByTagName("Variables").item(0).getTextContent());
					num_of_Variables = Integer
							.parseInt(eElement.getElementsByTagName("Variables").item(0).getTextContent());

					// System.out.println(
					// "Constraints : " +
					// eElement.getElementsByTagName("Constraints").item(0).getTextContent());
					num_of_Constraints = Integer
							.parseInt(eElement.getElementsByTagName("Constraints").item(0).getTextContent());

					for (temp = 0; temp < num_of_Variables; temp++) {
						var = var + String.valueOf(temp + 1);
						// System.out.println(var + " : " +
						// eElement.getElementsByTagName(var).item(0).getTextContent());
						name_of_Variables_list.add(eElement.getElementsByTagName(var).item(0).getTextContent());
						value_of_Variables_list.add(new ArrayList<Integer>(num_of_Constraints + 1)); // +1 for the
																										// objective
																										// function's
																										// parameters
						var = "Variable";
					}

					// System.out.println("OFVerb : " +
					// eElement.getElementsByTagName("OFVerb").item(0).getTextContent());
					OFVerb = eElement.getElementsByTagName("OFVerb").item(0).getTextContent();

					// System.out.println(
					// "VariableVerb : " +
					// eElement.getElementsByTagName("VariableVerb").item(0).getTextContent());
					VariableVerb = eElement.getElementsByTagName("VariableVerb").item(0).getTextContent();

				}
			}

			nList = doc.getElementsByTagName("Row");
			Integer add_A = 0;
			Integer add_b = 0;
			String A_MATLAB = "[";
			String b_MATLAB = "[";
			String f_MATLAB = "[";

			for (int tempo = 0; tempo < num_of_Variables; tempo++) { // tempo = no. of rows (variables)
				for (int temp = 0; temp < nList.getLength(); temp++) { // temp= no.of columns (constraints)
					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) {

						Element eElement = (Element) nNode;
						var = var + String.valueOf(tempo + 1);
						add_A = Math
								.round(Float.parseFloat(eElement.getElementsByTagName(var).item(0).getTextContent()));
						ind_values_Variables.add(add_A);
						var = "Variable";

						if (temp != (nList.getLength() - 1)) {

							A_MATLAB = A_MATLAB + add_A.toString() + ",";

						} else
							f_MATLAB = f_MATLAB + add_A.toString() + ",";

					}
				}
				value_of_Variables_list.get(tempo).addAll(ind_values_Variables);
				ind_values_Variables.clear();
				A_MATLAB = A_MATLAB + ";";
				f_MATLAB = f_MATLAB + ";";
			}

			A_MATLAB = A_MATLAB + "]";
			f_MATLAB = f_MATLAB + "]";

//			System.out.print(A_MATLAB);
//			System.out.print(f_MATLAB);

			// System.out.print("\n value_of_Variables_list");
			// for (int any = 0; any < value_of_Variables_list.size(); any++) {
			// System.out.print("\n" + value_of_Variables_list.get(any)); }

			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);

				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;

					Name_list.add(eElement.getElementsByTagName("Name").item(0).getTextContent());
					// System.out.println("\n Name : " +
					// eElement.getElementsByTagName("Name").item(0).getTextContent());

					Type_list.add(eElement.getElementsByTagName("Type").item(0).getTextContent());
					// System.out.println("Type : " +
					// eElement.getElementsByTagName("Type").item(0).getTextContent());

					add_b = Integer.parseInt(eElement.getElementsByTagName("RHS").item(0).getTextContent());
					RHS_list.add(add_b);

					if (temp != (nList.getLength() - 1)) {

						b_MATLAB = b_MATLAB + add_b.toString() + ";";

					}
					// System.out.println("RHS : " +
					// eElement.getElementsByTagName("RHS").item(0).getTextContent());
				}
			}

			b_MATLAB = b_MATLAB + "]";
//			System.out.print(b_MATLAB);

			// ************************************************************************************************************************
			// Solving using MATLAB
			// ************************************************************************************************************************

			MatlabEngine eng = MatlabEngine.startMatlab();
			// Object result_filename_return = eng.feval("LPP_NLG", A_MATLAB, b_MATLAB,
			// f_MATLAB, num_of_Variables, num_of_Constraints);
			// String result_filename = String.valueOf(result_filename_return);
			String result_filename = eng.feval("LPP_NLG");
			eng.close();

			// ************************************************************************************************************************
			// Reading solution from MATLAB
			// ************************************************************************************************************************

			String OptimalSolution_String; // Declare strings for every result required to parse float information
			String Sorted_Variables_String;
			String Sorted_Solution_String;
			String Sorted_Vars_String;
			String OptimalValue_String;
			String ExitFlag_String;
			// String PreMod;

			ArrayList<Integer> OptimalSolution_AL = new ArrayList<Integer>(); // Store the results finally in ArrayLists
			// ArrayList<Float> Solution_Set_AL = new ArrayList<Float>();
			// ArrayList<Float> Solution_Value_Set_AL = new ArrayList<Float>();
			ArrayList<ArrayList<Integer>> Sorted_Variables_AL = new ArrayList<ArrayList<Integer>>();
			ArrayList<Integer> Sorted_Solution_AL = new ArrayList<Integer>();
			ArrayList<Integer> Sorted_Vars_AL = new ArrayList<Integer>();
			ArrayList<Integer> OptimalValue_AL = new ArrayList<Integer>();
			ArrayList<Integer> ExitFlag_AL = new ArrayList<Integer>();

			// MatFileReader matfilereader = new
			// MatFileReader("E:\\Spain_2018\\SDC_Work\\LPP_result.mat"); // MATLAB
			// elements
			// to store
			// raw
			// format of
			// results

			MatFileReader matfilereader = new MatFileReader(result_filename);

			MLArray OptimalSolution = matfilereader.getMLArray("P");
			// MLArray Solution_Set = matfilereader.getMLArray("Solution_Set");
			// MLArray Solution_Value_Set = matfilereader.getMLArray("Solution_Value_Set");
			MLArray Sorted_Vars = matfilereader.getMLArray("Sorted_Vars");
			MLArray Sorted_Solution = matfilereader.getMLArray("Sorted_Solution");
			MLArray OptimalValue = matfilereader.getMLArray("solution");
			MLArray ExitFlag = matfilereader.getMLArray("exitflag");

			ArrayList<MLArray> Sorted_Variables_list = new ArrayList<MLArray>();
			ArrayList<String> Sorted_Variables_list_String = new ArrayList<String>();
			ArrayList<Matcher> Sorted_Variables_ind_matcher = new ArrayList<Matcher>();

			OptimalSolution_String = OptimalSolution.contentToString(); // Raw format to string conversion
			// Solution_Set_String = Solution_Set.contentToString();
			// Solution_Value_Set_String = Solution_Value_Set.contentToString();
			OptimalValue_String = OptimalValue.contentToString();
			Sorted_Vars_String = Sorted_Vars.contentToString();
			ExitFlag_String = ExitFlag.contentToString();
			Sorted_Solution_String = Sorted_Solution.contentToString();

			for (int tempo = 0; tempo < num_of_Variables; tempo++) { // tempo = no. of rows (variables)

				Sorted_Variables_AL.add(new ArrayList<Integer>());

			}

			String regex = "(-?[0-9]+[.][0-9]+)";

			Pattern pattern = Pattern.compile(regex); // Parse numerical information from string
			Matcher OptimalSolution_matcher = pattern.matcher(OptimalSolution_String);
			// Matcher Solution_Set_matcher = pattern.matcher(Solution_Set_String);
			// Matcher Solution_Value_Set_matcher =
			// pattern.matcher(Solution_Value_Set_String);
			Matcher Sorted_Solution_matcher = pattern.matcher(Sorted_Solution_String);
			Matcher Sorted_Vars_matcher = pattern.matcher(Sorted_Vars_String);
			// Matcher Sorted_Variables_matcher =
			// pattern.matcher(Sorted_Variables_list_String);
			Matcher OptimalValue_matcher = pattern.matcher(OptimalValue_String);
			Matcher ExitFlag_matcher = pattern.matcher(ExitFlag_String);

			while (OptimalSolution_matcher.find()) {
				OptimalSolution_AL.add(Math.round(Float.parseFloat(OptimalSolution_matcher.group()))); // Parse float
																										// info and
																										// store for use
			}
			// System.out.println(OptimalSolution_AL);

			// while (Solution_Set_matcher.find()) {
			// Solution_Set_AL.add(Float.parseFloat(Solution_Set_matcher.group()));
			// }
			// System.out.println(Solution_Set_AL);
			//
			// while (Solution_Value_Set_matcher.find()) {
			// Solution_Value_Set_AL.add(Float.parseFloat(Solution_Value_Set_matcher.group()));
			// }
			// System.out.println(Solution_Value_Set_AL);

			while (OptimalValue_matcher.find()) {
				OptimalValue_AL.add(Math.round(Float.parseFloat(OptimalValue_matcher.group())));
			}
			// System.out.println(OptimalValue_AL);

			while (Sorted_Vars_matcher.find()) {
				Sorted_Vars_AL.add(Math.round(Float.parseFloat(Sorted_Vars_matcher.group())));
			}
			// System.out.println(Sorted_Vars_AL);

			while (Sorted_Solution_matcher.find()) {
				Sorted_Solution_AL.add(Math.round(Float.parseFloat(Sorted_Solution_matcher.group())));
			}
			// System.out.println(Sorted_Solution_AL);

			while (ExitFlag_matcher.find()) {
				ExitFlag_AL.add(Math.round(Float.parseFloat(ExitFlag_matcher.group())));
			}
			// System.out.println(ExitFlag_AL);

			ArrayList<Integer> ind_Var = new ArrayList<Integer>();
			int b;

			for (int a = 0; a < num_of_Variables; a++) {

				b = a;
				while (b < Sorted_Vars_AL.size()) {

					ind_Var.add(Sorted_Vars_AL.get(b));
					b += num_of_Variables;

				}

				Sorted_Variables_AL.get(a).addAll(ind_Var);
				ind_Var.clear();

			}

//			System.out.println(Sorted_Variables_AL);

			// ***********************************************************************************************************************
			// REALIZATION BEGINS
			// ***********************************************************************************************************************

			float zero = 0;
			float one = 1;

			Lexicon lexicon = Lexicon.getDefaultLexicon();
			NLGFactory nlgFactory = new NLGFactory(lexicon);
			Realiser realiser = new Realiser(lexicon);

			SPhraseSpec phrase_final_2 = nlgFactory.createClause();
			SPhraseSpec phrase7 = nlgFactory.createClause();
			SPhraseSpec phrase8 = nlgFactory.createClause();
			SPhraseSpec phrase9 = nlgFactory.createClause();
			SPhraseSpec phrase10 = nlgFactory.createClause();
			SPhraseSpec phrase11 = nlgFactory.createClause();
			SPhraseSpec phrase12 = nlgFactory.createClause();

			NPPhraseSpec subject = nlgFactory.createNounPhrase(name_of_Authority);
			NPPhraseSpec subject_pronominal = nlgFactory.createNounPhrase(name_of_Authority);
			NPPhraseSpec object32 = nlgFactory.createNounPhrase(Name_list.get(num_of_Constraints));
			NPPhraseSpec object33 = nlgFactory.createNounPhrase(Name_list.get(num_of_Constraints));
			NPPhraseSpec object3 = nlgFactory.createNounPhrase(Name_list.get(num_of_Constraints));

			subject.setFeature(LexicalFeature.GENDER, Gender.NEUTER);
			NLGElement it = nlgFactory.createWord("it", LexicalCategory.PRONOUN);
			it.setFeature(Feature.POSSESSIVE, true);
			it.setPlural(true);

			VPPhraseSpec verb1 = nlgFactory.createVerbPhrase(VariableVerb);
			VPPhraseSpec verb2 = nlgFactory.createVerbPhrase(OFVerb);
			VPPhraseSpec verb3 = nlgFactory.createVerbPhrase(OFVerb);
			VPPhraseSpec verb5 = nlgFactory.createVerbPhrase("cross");
			VPPhraseSpec verb6 = nlgFactory.createVerbPhrase("exceed");
			VPPhraseSpec verb7 = nlgFactory.createVerbPhrase("decide");
			VPPhraseSpec verb8 = nlgFactory.createVerbPhrase("would");
			VPPhraseSpec verb9 = nlgFactory.createVerbPhrase("be");

			Integer difference_maxSol = 0;
			Integer maxSol = 0;

			for (int j = 1; j < Sorted_Solution_AL.size(); j++) {
				if (Sorted_Solution_AL.get(j) > maxSol) {
					maxSol = Sorted_Solution_AL.get(j);
				}
			}

			List<DocumentElement> sentence_list = new ArrayList<DocumentElement>();

			ArrayList<NPPhraseSpec> main_object_list = new ArrayList<NPPhraseSpec>();

			String PreMod = "";

			// ###############################################################################
			// PARAGRAPH-1
			// ###############################################################################

			for (int i = 0; i <= (num_of_Constraints); i++) {

				for (int j = 0; j < num_of_Variables; j++) {

					NPPhraseSpec object_ind = nlgFactory.createNounPhrase(name_of_Variables_list.get(j));
					object_ind.setPlural(true);

					if ((zero - Sorted_Variables_AL.get(j).get(i)) == 0)
						PreMod = "no";
					else if ((one - Sorted_Variables_AL.get(j).get(i)) == 0) {
						object_ind.setPlural(false);
						PreMod = String.valueOf(Sorted_Variables_AL.get(j).get(i));
					} else
						PreMod = String.valueOf(Sorted_Variables_AL.get(j).get(i));

					object_ind.addPreModifier(PreMod);

					main_object_list.add(object_ind);
				}

				NPPhraseSpec coordinated_object_temp = nlgFactory.createNounPhrase();

				for (int any = 0; any <= (num_of_Variables - 2); any++) {

					coordinated_object_temp.addPreModifier(main_object_list.get(any));

				}

				CoordinatedPhraseElement coordinated_object = nlgFactory
						.createCoordinatedPhrase(coordinated_object_temp, main_object_list.get(num_of_Variables - 1));

				// for(int any=0;any<(num_of_Variables-1);any++) {
				//
				// if(any==0) {
				//
				// coordinated_object =
				// nlgFactory.createCoordinatedPhrase(main_object_list.get(any),main_object_list.get(any+1));
				//
				// }
				//
				// else if(any>1) {
				//
				// coordinated_object =
				// nlgFactory.createCoordinatedPhrase(coordinated_object,main_object_list.get(any));
				//
				// }
				//
				// }

				NPPhraseSpec object3_sol = nlgFactory.createNounPhrase(Name_list.get(num_of_Constraints));
				object3_sol.setPlural(true);
				if (Sorted_Solution_AL.get(i) == 0)
					PreMod = "no";
				else if (Sorted_Solution_AL.get(i) == 1) {
					object3_sol.setPlural(false);
					PreMod = String.valueOf(Sorted_Solution_AL.get(i));
				} else
					PreMod = String.valueOf(Sorted_Solution_AL.get(i));
				object3_sol.addPreModifier(PreMod);

				if (i == 0) {

					SPhraseSpec phrase_final_1 = nlgFactory.createClause();
					SPhraseSpec phrase2 = nlgFactory.createClause();

					verb1.setPlural(true);
					phrase_final_1.setSubject(subject);
					phrase_final_1.setObject(coordinated_object);
					phrase_final_1.setVerb(verb1);
					phrase_final_1.setFeature(Feature.MODAL, "should");
					phrase2.setFeature(Feature.COMPLEMENTISER, "to " + OFVerb);
					phrase2.setObject(object3_sol);

					phrase_final_1.addComplement(phrase2);

					sentence_list.add(nlgFactory.createSentence(phrase_final_1));

				}

				else if (i == 1) {

					NPPhraseSpec subject1 = nlgFactory.createNounPhrase("authority");
					subject1.setDeterminer("the");

					verb7.setPlural(true);
					verb7.setFeature(Feature.TENSE, Tense.FUTURE);
					verb7.addPostModifier("to " + VariableVerb);

					verb8.addPostModifier(OFVerb);

					verb9.setFeature(Feature.TENSE, Tense.PRESENT);
					verb9.setPlural(false);

					phrase9.setSubject(subject1);
					phrase9.setVerb(verb7);

					phrase9.setObject(coordinated_object);

					object32.setPlural(true);
					if (Sorted_Solution_AL.get(i) == 0)
						PreMod = "no";
					else if (Sorted_Solution_AL.get(i) == 1) {
						object32.setPlural(false);
						PreMod = String.valueOf(Sorted_Solution_AL.get(i));
					} else
						PreMod = String.valueOf(Sorted_Solution_AL.get(i));
					object32.addPreModifier(PreMod);

					phrase9.addFrontModifier("Suppose");
					phrase10.setFeature(Feature.COMPLEMENTISER, "then");

					subject_pronominal.setFeature(Feature.PRONOMINAL, true);
					subject_pronominal.setPlural(true);
					phrase10.setSubject(subject_pronominal);
					verb3.setFeature(Feature.TENSE, Tense.FUTURE);
					phrase10.setVerb(verb8);
					phrase10.setObject(object32);

					phrase9.addComplement(phrase10);

					sentence_list.add(nlgFactory.createSentence(phrase9));

				} else if (i == (num_of_Constraints)) {

					phrase7.setSubject(subject_pronominal);
					phrase7.setVerb(verb1);

					phrase7.addFrontModifier("however, if");
					phrase8.setFeature(Feature.COMPLEMENTISER, "then");

					phrase7.setObject(coordinated_object);
					subject_pronominal.setFeature(Feature.PRONOMINAL, true);
					subject_pronominal.setPlural(true);
					phrase8.setSubject(subject_pronominal);
					phrase8.setVerb(verb2);
					phrase8.setObject(object3_sol);
					verb5.setFeature(Feature.TENSE, Tense.FUTURE);

					phrase7.addComplement(phrase8);

					difference_maxSol = Math.abs(Sorted_Solution_AL.get(i) - maxSol);

					if (difference_maxSol > 10) {
						object33.addPreModifier(" much less than the maximum");
					} else
						object33.addPreModifier(String.valueOf(difference_maxSol) + " less than the maximum");

					phrase11.setObject(object33);
					phrase11.setVerb(verb9);

					phrase7.addComplement(phrase11);

					phrase12.setSubject(subject_pronominal);
					verb3.setPlural(true);
					phrase12.setVerb(verb3);
					phrase12.setFeature(Feature.MODAL, "can");

					phrase7.addComplement(phrase12);

					sentence_list.add(nlgFactory.createSentence(phrase7));

				}

				main_object_list.clear();

			}

			verb3.setPlural(true);
			// NPPhraseSpec object4 = nlgFactory.createNounPhrase("number");
			// NPPhraseSpec object3 = nlgFactory.createNounPhrase("passenger");

			// IntSummaryStatistics stat = Arrays.stream(val3).summaryStatistics();
			// int max_val3 = stat.getMax();

			object3.setPlural(true);

			if (Sorted_Solution_AL.get(0) == 0)
				PreMod = "no";
			else if (Sorted_Solution_AL.get(0) == 1) {
				object3.setPlural(false);
				PreMod = String.valueOf(Sorted_Solution_AL.get(0));
			} else
				PreMod = String.valueOf(Sorted_Solution_AL.get(0));
			object3.addPreModifier("maximum " + "of " + PreMod);

			phrase_final_2.setSubject(subject);
			phrase_final_2.setObject(object3);
			phrase_final_2.setVerb(verb3);
			phrase_final_2.setFeature(Feature.MODAL, "can");
			phrase_final_2.addFrontModifier("therefore,");

			sentence_list.add(nlgFactory.createSentence(phrase_final_2));

			DocumentElement par1 = nlgFactory.createParagraph(sentence_list);
			String sentence = realiser.realise(par1).getRealisation();
			System.out.println(sentence);

			// ###############################################################################
			// PARAGRAPH-2
			// ###############################################################################
			
			

		} catch (Exception e) {
			e.printStackTrace();

		}
	}
}