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

import java.util.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Math;

import com.mathworks.engine.*;

import com.jmatio.io.*;
import com.jmatio.types.*;

import java.util.regex.*;

import matlabcontrol.*;

public class Generalized_LPP_MATLAB_Direct {

	public static void main(String[] args) {

		try {
			ArrayList<String> Name_list = new ArrayList<String>();
			ArrayList<ArrayList<Float>> value_of_Variables_list = new ArrayList<ArrayList<Float>>();
			ArrayList<Float> ind_values_Variables = new ArrayList<Float>();
			ArrayList<String> Type_list = new ArrayList<String>();
			ArrayList<Float> RHS_list = new ArrayList<Float>();
			ArrayList<String> name_of_Variables_list = new ArrayList<String>();

			String name_of_Authority = null;
			int num_of_Variables = 0;
			int num_of_Constraints = 0;
			String OFVerb = null; //objective function verb
			String VariableVerb = null; //verb corresponding to the variables
			String var = "Variable"; //number of the variable (prefix)

			File inputFile = new File("E:\\Spain_2018\\SDC_Work\\Problems_LPP\\WHSC.txt"); //path of XML structure of the problem in .txt file
			
//			File inputFile = null;
			Scanner input = new Scanner(System.in);
//			
//			System.out.println("Enter the XML file (.txt format): "); //in case the user wants to input his/her own XML structure
//			String FileName = input.nextLine();
//			inputFile = new File(FileName);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			NodeList nList = doc.getElementsByTagName("Metadata"); //scan Metadata of the problem

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
						value_of_Variables_list.add(new ArrayList<Float>(num_of_Constraints + 1)); // +1 for the
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

			nList = doc.getElementsByTagName("Row"); //each row corresponds to the constraints, last one for objective function
			Float add_A; //A = coefficient matrix for constraints
			Float add_b; //RHS vector for constraints
			String A_MATLAB = "[";
			String b_MATLAB = "[";
			String f_MATLAB = "["; //f = coefficient vector for constraints

			for (int tempo = 0; tempo < num_of_Variables; tempo++) { // tempo = no. of rows (variables)
				for (int temp = 0; temp < nList.getLength(); temp++) { // temp= no.of columns (constraints)
					Node nNode = nList.item(temp);
					if (nNode.getNodeType() == Node.ELEMENT_NODE) { //get coefficient of variable

						Element eElement = (Element) nNode;
						var = var + String.valueOf(tempo + 1);
//						add_A = Math
//								.round(Float.parseFloat(eElement.getElementsByTagName(var).item(0).getTextContent()));
						add_A = (Float.parseFloat(eElement.getElementsByTagName(var).item(0).getTextContent()));
						ind_values_Variables.add(add_A);
						var = "Variable";

						if (temp != (nList.getLength() - 1)) {

							A_MATLAB = A_MATLAB + add_A.toString() + ","; //create A matrix to be sent to MATLAB for evaluation

						} else
							f_MATLAB = f_MATLAB + add_A.toString() + ","; //create f vector to be sent to MATLAB for evaluation

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

					Name_list.add(eElement.getElementsByTagName("Name").item(0).getTextContent()); // name of resource corresponding to the constraint
					// System.out.println("\n Name : " +
					// eElement.getElementsByTagName("Name").item(0).getTextContent());

					Type_list.add(eElement.getElementsByTagName("Type").item(0).getTextContent()); // type of constraint: less than, greater than etc.
					// System.out.println("Type : " +
					// eElement.getElementsByTagName("Type").item(0).getTextContent());

					add_b = Float.parseFloat(eElement.getElementsByTagName("RHS").item(0).getTextContent()); // RHS of constraint
					RHS_list.add(add_b);

					if (temp != (nList.getLength() - 1)) {

						b_MATLAB = b_MATLAB + add_b.toString() + ";"; //create b vector to be sent to MATLAB for evaluation

					}
					// System.out.println("RHS : " +
					// eElement.getElementsByTagName("RHS").item(0).getTextContent());
				}
			}

			b_MATLAB = b_MATLAB + "]";
//			System.out.print(b_MATLAB);
//			System.out.print(num_of_Variables);
//			System.out.print(num_of_Constraints);

			// ************************************************************************************************************************
			// Solving using MATLAB (MATLABControl)
			// ************************************************************************************************************************
			
			Object[] arr = new Object[5]; // create object array of all 'objects' of data related to the LPP to be sent to MATLAB for computation of the solution
			
			arr[0] = A_MATLAB;
			arr[1] = b_MATLAB;
			arr[2] = f_MATLAB;
			arr[3] = num_of_Variables;
			arr[4] = num_of_Constraints;
			
			MatlabProxyFactoryOptions options =  new MatlabProxyFactoryOptions.Builder().setUsePreviouslyControlledSession(true).build();
			MatlabProxyFactory factory = new MatlabProxyFactory(options);
	        MatlabProxy proxy = factory.getProxy(); //establish connection with MATLAB
	        
	        proxy.returningFeval("LPP_NLG", 0, arr);  //evaluate the LPP on MATLAB
								
	        
//	        proxy.disconnect();			
			
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

			 MatFileReader matfilereader = new MatFileReader("E:\\Spain_2018\\SDC_Work\\LPP_result.mat"); // MATLAB
//			 elements
//			 to store
//			 raw
//			 format of
//			 results
			
//			System.out.println(result_filename_return);

//			MatFileReader matfilereader = new MatFileReader(result_filename);

			MLArray OptimalSolution = matfilereader.getMLArray("P");
			// MLArray Solution_Set = matfilereader.getMLArray("Solution_Set");
			// MLArray Solution_Value_Set = matfilereader.getMLArray("Solution_Value_Set");
			MLArray Sorted_Vars = matfilereader.getMLArray("Sorted_Vars");
			MLArray Sorted_Solution = matfilereader.getMLArray("Sorted_Solution");
			MLArray OptimalValue = matfilereader.getMLArray("solution");
			MLArray ExitFlag = matfilereader.getMLArray("exitflag");

//			ArrayList<MLArray> Sorted_Variables_list = new ArrayList<MLArray>();
//			ArrayList<String> Sorted_Variables_list_String = new ArrayList<String>();
//			ArrayList<Matcher> Sorted_Variables_ind_matcher = new ArrayList<Matcher>();

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

			ArrayList<Integer> ind_Var = new ArrayList<Integer>(); // picking out individualvalues of the variables in a sorted manner
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
			
			// ***********************************************************************************************************************
			// REALIZATION BEGINS
			// ***********************************************************************************************************************

			float zero = 0;
			float one = 1;

			Lexicon lexicon = Lexicon.getDefaultLexicon();
			NLGFactory nlgFactory = new NLGFactory(lexicon);
			Realiser realiser = new Realiser(lexicon);

			SPhraseSpec phrase_final_2 = nlgFactory.createClause();
//			SPhraseSpec phrase3 = nlgFactory.createClause();
//			SPhraseSpec phrase4 = nlgFactory.createClause();
//			SPhraseSpec phrase6 = nlgFactory.createClause();
			SPhraseSpec phrase7 = nlgFactory.createClause();
			SPhraseSpec phrase8 = nlgFactory.createClause();
			SPhraseSpec phrase9 = nlgFactory.createClause();
			SPhraseSpec phrase10 = nlgFactory.createClause();
			SPhraseSpec phrase11 = nlgFactory.createClause();
			SPhraseSpec phrase12 = nlgFactory.createClause();
			SPhraseSpec phrase13 = nlgFactory.createClause();
			SPhraseSpec phrase14 = nlgFactory.createClause();
			SPhraseSpec phrase15 = nlgFactory.createClause();
			SPhraseSpec phrase16 = nlgFactory.createClause();

			NPPhraseSpec subject = nlgFactory.createNounPhrase(name_of_Authority);
			NPPhraseSpec subject_pronominal = nlgFactory.createNounPhrase(name_of_Authority);
			NPPhraseSpec object32 = nlgFactory.createNounPhrase(Name_list.get(num_of_Constraints));
			NPPhraseSpec object33 = nlgFactory.createNounPhrase(Name_list.get(num_of_Constraints));
			NPPhraseSpec object3 = nlgFactory.createNounPhrase(Name_list.get(num_of_Constraints));
			NPPhraseSpec subject1 = nlgFactory.createNounPhrase("authority");

			subject.setFeature(LexicalFeature.GENDER, Gender.NEUTER);
			NLGElement it = nlgFactory.createWord("it", LexicalCategory.PRONOUN);
			it.setFeature(Feature.POSSESSIVE, true);
			it.setPlural(true);
			
			NPPhraseSpec object = nlgFactory.createNounPhrase(it,"threshold");

			VPPhraseSpec verb1 = nlgFactory.createVerbPhrase(VariableVerb);
			VPPhraseSpec verb2 = nlgFactory.createVerbPhrase(OFVerb);
			VPPhraseSpec verb3 = nlgFactory.createVerbPhrase(OFVerb);
			VPPhraseSpec verb4 = nlgFactory.createVerbPhrase(VariableVerb);
			VPPhraseSpec verb5 = nlgFactory.createVerbPhrase("cross");
			VPPhraseSpec verb6 = nlgFactory.createVerbPhrase("exceed");
			VPPhraseSpec verb7 = nlgFactory.createVerbPhrase("decide");
			VPPhraseSpec verb8 = nlgFactory.createVerbPhrase("would");
			VPPhraseSpec verb9 = nlgFactory.createVerbPhrase("be");

			Integer difference_maxSol = 0;
			Integer maxSol = 0;
			int done_flag = 0;

			for (int j = 1; j < Sorted_Solution_AL.size(); j++) {
				if (Sorted_Solution_AL.get(j) > maxSol) {
					maxSol = Sorted_Solution_AL.get(j);
				}
			}

			List<DocumentElement> sentence_list = new ArrayList<DocumentElement>();
			List<DocumentElement> sentence_list2 = new ArrayList<DocumentElement>();

			ArrayList<NPPhraseSpec> main_object_list = new ArrayList<NPPhraseSpec>();

			String PreMod = "";
			
			//System.out.println(Sorted_Solution_AL.size());
			

			// ###############################################################################
			// PARAGRAPH-1
			// ###############################################################################

			for (int i = 0; i<Sorted_Solution_AL.size(); i++) {
								
				//for (int i = 0; i <Sorted_Solution_AL.size(); i++) {

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

					} else if ((i>1)&&(Sorted_Solution_AL.get(i)!=0)) {
						
						//done_flag = 1;

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
						break;

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
			
			// *******************************************************************************
			// INTERACTIVITY BEGINS HERE
			// *******************************************************************************
			
			char que = 'y';
			char change_que = 'y';
			ArrayList<SPhraseSpec> Print_Menu = new ArrayList<SPhraseSpec>();
			SPhraseSpec Print = nlgFactory.createClause();
			
			System.out.println("Do you want to try more values as solutions? (y/n) ");
			que = input.next().charAt(0);
			
			while(que=='y') {
				
				System.out.println("Enter the variable whose value you wish to change.");
				for (int i=0;i<num_of_Variables;i++) {
					
					Print.setSubject(name_of_Variables_list.get(i));
					System.out.println((i+1) + ". " + realiser.realiseSentence(Print));
					
				}
				
				change_que = input.next().charAt(0);
				int cho = Character.getNumericValue(change_que);
				int t;
				int change_val;
				String ConstraintDiff_String;
				ArrayList<Integer> ConstraintDiff_AL = new ArrayList<Integer>();
				int new_OS_val = 0;
				int flag = 0;
				
				if((cho-1) < num_of_Variables) {
					
					String new_Solution_MATLAB = "[";
					ArrayList<Integer> Changed_Solution = new ArrayList<Integer>(num_of_Variables);
					
					System.out.println("Enter the new value: ");
					change_val = input.nextInt();
					
//					System.out.println("Original optimal solution: ");
//					for(int i=0;(i<num_of_Variables);i++)
//						System.out.println(Sorted_Variables_AL.get(i).get(0));
					
					for(int j=0;(j<num_of_Variables);j++) {
						
						t = Sorted_Variables_AL.get(j).get(0);
						Changed_Solution.add(j,t);
					}
					
					Changed_Solution.set((cho-1),change_val);
					
					for (int j = 0; j<Changed_Solution.size(); j++)
						new_Solution_MATLAB = new_Solution_MATLAB + Changed_Solution.get(j).toString() + ",";
					
					new_Solution_MATLAB = new_Solution_MATLAB + "]";
					
//					System.out.println(new_Solution_MATLAB);
					
//					System.out.println("Changed 'optimal' solution: ");
//					System.out.println(Changed_Solution);
					
					
					Object[] analysis_param = new Object[6];
					
					analysis_param[0] = A_MATLAB;
					analysis_param[1] = b_MATLAB;
					analysis_param[2] = f_MATLAB;
					analysis_param[3] = num_of_Variables;
					analysis_param[4] = num_of_Constraints;
					analysis_param[5] = new_Solution_MATLAB;
					
					// **************************************** RUN MATLAB WITH NEW VALUES *********************************************
			        
			        proxy.returningFeval("LPP_NLG_NewVal", 0, analysis_param);	// evaluate LPP with changed values        
			        
			        matfilereader = new MatFileReader("E:\\Spain_2018\\SDC_Work\\LPP_NewVal_result.mat");
			        
			        MLArray Constraint_Difference = matfilereader.getMLArray("constraint_diff");
			        ConstraintDiff_String = Constraint_Difference.contentToString();
			        Matcher ConstraintDiff_matcher = pattern.matcher(ConstraintDiff_String);
			        while (ConstraintDiff_matcher.find()) {
						ConstraintDiff_AL.add(Math.round(Float.parseFloat(ConstraintDiff_matcher.group())));
					}
			        
			        MLArray new_OS = matfilereader.getMLArray("new_fval");
			        String new_OS_String = new_OS.contentToString();
			        Matcher new_OS_matcher = pattern.matcher(new_OS_String);
			        while (new_OS_matcher.find()) {
						new_OS_val = Math.round(Float.parseFloat(new_OS_matcher.group()));
					}
			        
//			        System.out.println(ConstraintDiff_AL);
//			        System.out.println(new_OS_val-Sorted_Solution_AL.get(0));
//			        System.out.println("new_OS_val" + new_OS_val);
//			        System.out.println("Sorted_Solution_AL.get(0)" + Sorted_Solution_AL.get(0));
			        
			        // ###############################################################################
					// Generate sentences for new solution
					// ###############################################################################
			        
			        NPPhraseSpec object_changed = nlgFactory.createNounPhrase(name_of_Variables_list.get(cho-1));
			        VPPhraseSpec be = nlgFactory.createVerbPhrase("able");
        			be.setFeature(Feature.TENSE,Tense.FUTURE);
        			be.setPlural(false);
        			be.addPostModifier("to "+ OFVerb);
        			be.addPreModifier("will be");
        			object3 = nlgFactory.createNounPhrase(Name_list.get(num_of_Constraints));
        			object3.addPreModifier(String.valueOf(new_OS_val));
        			object3.setPlural(true);
			        
			        phrase13.addFrontModifier("Considering that,");
			        phrase13.setSubject(subject);
			        
			        verb1.setPlural(false);
			        verb1.setFeature(Feature.TENSE, Tense.PRESENT);
			        phrase13.setVerb(verb1);
			        
			        object_changed.addPreModifier(String.valueOf(change_val));
			        phrase13.setObject(object_changed);
			        
			        phrase14.setSubject(subject_pronominal);
			        phrase14.setVerb(be);			        
			        phrase14.setObject(object3);
			        
			        phrase13.addPostModifier(phrase14);
			        
			        sentence_list2.add(nlgFactory.createSentence(phrase13));
			        
			        
			        // ************************************************************ SENTENCE 2 **************************************************************
			        
			        NPPhraseSpec value = nlgFactory.createNounPhrase("value");
			        value.addPreModifier("original");
			        value.setDeterminer("the");
			        
			        VPPhraseSpec compare = nlgFactory.createVerbPhrase("compare");
			        compare.setFeature(Feature.TENSE, Tense.PAST);
			        compare.addPostModifier("to");
			        verb2 = nlgFactory.createVerbPhrase(OFVerb);
			        verb2.addPreModifier("can");
			        
			        phrase15.addFrontModifier("As a consequence,");
			        phrase15.setSubject(subject_pronominal);
			        phrase15.setVerb(verb2);
			        
			        object3 = nlgFactory.createNounPhrase(Name_list.get(num_of_Constraints));
			        
			        if(Math.abs(new_OS_val) > Sorted_Solution_AL.get(0))
			        	object3.addPreModifier(String.valueOf(Math.abs(new_OS_val - Sorted_Solution_AL.get(0))) + " more");
			        else if(Math.abs(new_OS_val) < Sorted_Solution_AL.get(0))
			        	object3.addPreModifier(String.valueOf(Math.abs(new_OS_val - Sorted_Solution_AL.get(0))) + " less");
			        else
			        	object3.addPreModifier("same number of");
        			object3.setPlural(true);
        			
        			phrase15.setObject(object3);
        			
        			phrase16.addFrontModifier("when");
        			phrase16.setVerb(compare);
        			phrase16.setObject(value);
        			phrase16.setFeature(Feature.TENSE, Tense.PAST);
        			
        			phrase15.addPostModifier(phrase16);
        			
        			sentence_list2.add(nlgFactory.createSentence(phrase15));	

					DocumentElement par2 = nlgFactory.createParagraph(sentence_list2);
					String sentence2 = realiser.realise(par2).getRealisation();
					System.out.println(sentence2);
			              
			        
			        
					// ###############################################################################
					// Generate sentences for constraints
					// ###############################################################################
			        
			        for (int i = 0; i < ConstraintDiff_AL.size(); i++) {
			        	
			        	if(ConstraintDiff_AL.get(i)<0) {
			        		
			        		if((Math.abs(ConstraintDiff_AL.get(i))>100)&&(flag == 0)) {
			        			
			        			flag = 1;
			        			
			        			NPPhraseSpec subject2 = nlgFactory.createNounPhrase(it,Name_list.get(i));
				        		//subject2.setDeterminer("the");
				        		
				        		SPhraseSpec phrase3 = nlgFactory.createClause();
				    			SPhraseSpec phrase4 = nlgFactory.createClause();
				    			SPhraseSpec phrase6 = nlgFactory.createClause();
				    			
				    			//verb4.setTense(Tense.FUTURE);
					    		
					    		phrase3.setSubject(subject1); //the authority
					    		//phrase3.setVerb(verb4);//buys			    		
					    		phrase3.addFrontModifier("However,");
					    		
					    		NPPhraseSpec object1 = nlgFactory.createNounPhrase(name_of_Variables_list.get(cho-1));
					    		NPPhraseSpec object1_old = nlgFactory.createNounPhrase();
					    		SPhraseSpec instead = nlgFactory.createClause();
					    		
					    		object1.addPreModifier(String.valueOf(Changed_Solution.get(cho-1)));
					    		object1_old.addPreModifier(String.valueOf(Sorted_Variables_AL.get(cho-1).get(0)));
					    		
					    		instead.setFeature(Feature.COMPLEMENTISER, "instead of");
					    		instead.setObject(object1_old);
					    		
					    		phrase3.setObject(object1); // new value of solution			    		
					    		phrase3.addComplement(instead); // instead of old value of variable changed
					    		
					    		//phrase4.setFeature(Feature.COMPLEMENTISER,"then"); // then
					    		
					    		verb6.setFeature(Feature.TENSE, Tense.FUTURE); //crosses
					    		
					    		//phrase4.setSubject(subject2); // the constraint resource
					    		phrase3.setVerb(verb6); // cross
					    		phrase3.setObject(subject2); // their threshold
					    		
					    		//phrase3.addComplement(phrase4); // then their constraint resource crosses their threshold 
					    		
					    		phrase6.setFeature(Feature.COMPLEMENTISER, "by"); // by
					    		phrase6.setObject(String.valueOf(Math.abs(ConstraintDiff_AL.get(i)))); //ConstraintDiff_AL
					    		
					    		phrase3.addComplement(phrase6);
					    		
					    		//sentence_list3.add(nlgFactory.createSentence(phrase3));
					    		
					    		String sentence3 = realiser.realiseSentence(phrase3);
					    		System.out.println(sentence3); //############################################################## SENTENCE ####################################################
			        			
			        		}
			        		 
			        		else if(Math.abs(new_OS_val) > Sorted_Solution_AL.get(0)) {
			        			
			        			SPhraseSpec phrase1 = nlgFactory.createClause();
			        			SPhraseSpec phrase2 = nlgFactory.createClause();
			        			SPhraseSpec phrase3 = nlgFactory.createClause();
			        			
			        			VPPhraseSpec increase = nlgFactory.createVerbPhrase("increase");
			        			//VPPhraseSpec be = nlgFactory.createVerbPhrase("able");
			        			
			        			NPPhraseSpec subject3 = nlgFactory.createNounPhrase(it,Name_list.get(i));
			        			NPPhraseSpec any = nlgFactory.createNounPhrase();
			        			NPPhraseSpec new_object = nlgFactory.createNounPhrase(Name_list.get(num_of_Constraints));
			        			new_object.setPlural(true);
			        			new_object.addPreModifier(String.valueOf(new_OS_val-Sorted_Solution_AL.get(0)) + " more");
			        			
			        			phrase2.addFrontModifier("Moreover, if");
			        			phrase2.setSubject(subject_pronominal);
			        			phrase2.setVerb(increase);
			        			phrase2.setObject(subject3);
			        			
			        			phrase1.setFeature(Feature.COMPLEMENTISER, "by");
			        			
			        			any.addPreModifier(String.valueOf(Math.abs(ConstraintDiff_AL.get(i))));
			        			
			        			phrase1.setObject(any);
			        			
			        			phrase2.addComplement(phrase1);
			        			phrase3.setFeature(Feature.COMPLEMENTISER, "then");
			        			
			        			phrase3.setSubject(subject_pronominal);
			        			phrase3.setVerb(be);
			        			phrase3.setObject(new_object);
			        			//phrase3.setFeature(Feature.TENSE, Tense.FUTURE);
			        			
			        			phrase2.addComplement(phrase3);
			        			
			        			String sentence4 = realiser.realiseSentence(phrase2);
					    		System.out.println(sentence4); //############################################################## SENTENCE ####################################################
					    		break;

			        		}
			        		
			        	}
			        	
			        	else
			        		continue;
			    		
			        	
			        }
					
				}
				
				System.out.println("Do you want to try more values? (y/n) ");
				que = input.next().charAt(0);
				
			}
			
			proxy.disconnect();
			System.out.println("Thank you!");
			
		} catch (Exception e) {
			e.printStackTrace();;

		}
	}
}
