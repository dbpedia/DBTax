package org.dbpedia.categories.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dbpedia.categories.Utils;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 7/15/14 11:09 AM
 */
public class CalculateStats {

    private static final int INTERATORS = 3;

    public static void main(String[] args) {
        /**
         * arg0 => Class Eval tsv file
         * arg1 => Path eval tsv file
         * */

        Set<EvaluationItem> classEvaluationItems = new HashSet<>();
        Set<EvaluationItem> pathEvaluationItems = new HashSet<>();

        List<ClassEvaluation> classRatings = new ArrayList<>();
        List<PathEvaluation> pathRatings = new ArrayList<>();

        {
        /* Real Class evaluations */
            List<String> classEvalLines = Utils.getFileLines(args[0]);
            for (String evalStr : classEvalLines) {
                String[] parts = evalStr.split("\t");

                // id, TYPE, CB, NAME, USER
                if (parts.length > 5) {
                    System.out.println("Not enough args in: " + evalStr);
                    System.exit(1);
                }
                EvaluationItem evalItem = new EvaluationItem(parts[0], parts[3]); // ID, NAME
                classEvaluationItems.add(evalItem);

                // make evalItem reference the set object
                evalItem = Utils.getSetItem(classEvaluationItems, evalItem);


                classRatings.add(new ClassEvaluation("", parts[1], parts[2], evalItem));
            }
        }

        { /* Real Path evaluations */

            List<String> pathEvalLines = Utils.getFileLines(args[1]);
            for (String evalStr : pathEvalLines) {
                String[] parts = evalStr.split("\t");

                // id, PA, PB, PC, PATH, USER
                if (parts.length > 6) {
                    System.out.println("Not enough args in: " + evalStr);
                    System.exit(1);
                }
                EvaluationItem evalItem = new EvaluationItem(parts[0], parts[4]); // ID, PATH
                pathEvaluationItems.add(evalItem);

                // make evalItem reference the set object
                evalItem = Utils.getSetItem(pathEvaluationItems, evalItem);

                pathRatings.add(new PathEvaluation("", parts[1], parts[2], parts[3],evalItem));
            }
        }


        {   // calculate inter-rater agreement for Class

            int questions = (int) classRatings.size() / CalculateStats.INTERATORS ;
            short [][] typeRatings = new  short[questions][CalculateStats.INTERATORS];
            short [][] breakRatings = new  short[questions][CalculateStats.INTERATORS];
            short [][] validRatings = new  short[questions][CalculateStats.INTERATORS];
            short [][] specificRatings = new  short[questions][CalculateStats.INTERATORS];
            short [][] broadRatings = new  short[questions][CalculateStats.INTERATORS];

            for (ClassEvaluation ce: classRatings) {
                int typeColumn = ce.getcType() == ClassEvaluation.TYPE.CLASS ? 1 : 0;
                typeRatings[ce.getEvaluationItem().getPos()-1][typeColumn] ++;

                int breakableColumn = ce.getBreakable() == ClassEvaluation.BREAKABLE.CANNOT_BREAK || ce.getBreakable() == ClassEvaluation.BREAKABLE.UNDEFINED ? 1 : 0;
                breakRatings[ce.getEvaluationItem().getPos() - 1][breakableColumn]++;


            }

            System.out.println("Kappa for class/Instance: " + FleissKappa.computeKappa(typeRatings));
            System.out.println("Kappa for breakable: " + FleissKappa.computeKappa(breakRatings));

            for (PathEvaluation pe: pathRatings) {
                int validColumn = pe.getValid() == PathEvaluation.VALID.VALID ? 1 : 0;
                validRatings[pe.getEvaluationItem().getPos()-1][validColumn] ++;

                int specificColumn = pe.getSpecific() == PathEvaluation.SPECIFIC.NOT_SPECIFIC || pe.getSpecific() == PathEvaluation.SPECIFIC.UNDEFINED ? 1 : 0;
                specificRatings[pe.getEvaluationItem().getPos()-1][specificColumn] ++;

                int broadColumn = pe.getBroad() == PathEvaluation.BROAD.NOT_BROAD || pe.getBroad() == PathEvaluation.BROAD.UNDEFINED ? 1 : 0;
                broadRatings[pe.getEvaluationItem().getPos()-1][broadColumn] ++;

            }
            System.out.println("Kappa for path valid: " + FleissKappa.computeKappa(validRatings));
            System.out.println("Kappa for path specific: " + FleissKappa.computeKappa(specificRatings));
            System.out.println("Kappa for path broad: " + FleissKappa.computeKappa(broadRatings));


        }


        {   // Calculate average Class "class/Instance"

            Map<EvaluationItem.EVALSOURCE, StatObj> classStatsCI = getNewStatMap();
            // Iterrate
            for (ClassEvaluation ce : classRatings) {
                StatObj stats = classStatsCI.get(ce.getEvaluationItem().getSource());
                stats.totalCount++;
                if (ce.getcType().equals(ClassEvaluation.TYPE.CLASS)) {
                    stats.occurenceCount++;
                }
            }

            printStats(classStatsCI, "Class: Class/Instance stats");


            /* interrater agreement
            ArrayList<EvaluationItem> evaluationItems = new ArrayList<>(classEvaluationItems);

            ClassEvaluation.TYPE [][] agreement = new ClassEvaluation.TYPE [evaluationItems .size()][2];
            // init array
            for (ClassEvaluation.TYPE [] row: agreement)
                Arrays.fill(row, ClassEvaluation.TYPE.UNDEFINED);

            for (ClassEvaluation ce : classRatings) {
                int index = evaluationItems.indexOf(ce.getEvaluationItem());
                if (agreement[index][0] == ClassEvaluation.TYPE.UNDEFINED) {
                    agreement[index][0]= ce.getcType();
                }
                else {
                    agreement[index][1] = ce.getcType();
                }
            }


            double total = 0;
            double both_class = 0;
            double both_instance = 0;
            double total_classes = 0;
            double total_instances = 0;
            for (ClassEvaluation.TYPE [] row: agreement) {
                total +=2;
                if (row[0].equals(ClassEvaluation.TYPE.CLASS)) {
                    total_classes ++;
                }
                else {
                    total_instances++;
                }
                if (row[1].equals(ClassEvaluation.TYPE.CLASS)) {
                    total_classes ++;
                }
                else {
                    total_instances++;
                }
                if (row[0].equals(row[1])) {
                    if (row[0].equals(ClassEvaluation.TYPE.CLASS)) {
                        both_class++;
                    } else {
                        both_instance ++;
                    }
                }
            }

            double PE = (both_class + both_instance) * 2 / (total );
            double PCa = total_instances / (total );
            double PC = Math.pow(PCa,2) + Math.pow(1-PCa, 2);

            double kappa = (PE - PC) / (1 - PC);

            System.out.println("Class kappa: " + kappa);
            */

        }

        {   // Calculate average Class "BREAKABLE"

            Map<EvaluationItem.EVALSOURCE, StatObj> classStatsBR = getNewStatMap();
            // Iterrate
            for (ClassEvaluation ce : classRatings) {
                StatObj stats = classStatsBR.get(ce.getEvaluationItem().getSource());
                if (!ce.getBreakable().equals(ClassEvaluation.BREAKABLE.UNDEFINED)) {
                    stats.totalCount++;
                    if (ce.getBreakable().equals(ClassEvaluation.BREAKABLE.CANNOT_BREAK)) {
                        stats.occurenceCount++;
                    }
                }

            }


            printStats(classStatsBR, "Class: Breakable stats");

        }

        {   // Calculate average Path "valid"

            Map<EvaluationItem.EVALSOURCE, StatObj> pathStatsVALID = getNewStatMap();
            // Iterrate
            for (PathEvaluation pe : pathRatings) {
                StatObj stats = pathStatsVALID.get(pe.getEvaluationItem().getSource());
                stats.totalCount++;
                if (pe.getValid().equals(PathEvaluation.VALID.VALID)) {
                    stats.occurenceCount++;
                }
            }

            printStats(pathStatsVALID, "PAth: Valid stats");

        }

        {   // Calculate average Path "Specific"

            Map<EvaluationItem.EVALSOURCE, StatObj> pathStatsSpecific = getNewStatMap();
            // Iterrate
            for (PathEvaluation pe : pathRatings) {
                StatObj stats = pathStatsSpecific.get(pe.getEvaluationItem().getSource());
                if (!pe.getSpecific().equals(PathEvaluation.SPECIFIC.UNDEFINED)) {
                    stats.totalCount++;
                    if (pe.getSpecific().equals(PathEvaluation.SPECIFIC.NOT_SPECIFIC)) {
                        stats.occurenceCount++;
                    }
                }


            }

            printStats(pathStatsSpecific, "PAth: Specific stats");
        }

        {   // Calculate average Path "Broad"

            Map<EvaluationItem.EVALSOURCE, StatObj> pathStatsBROAD = getNewStatMap();
            // Iterrate
            for (PathEvaluation pe : pathRatings) {
                StatObj stats = pathStatsBROAD.get(pe.getEvaluationItem().getSource());
                if (!pe.getBroad().equals(PathEvaluation.BROAD.UNDEFINED)) {
                    stats.totalCount++;
                    if (pe.getBroad().equals(PathEvaluation.BROAD.NOT_BROAD)) {
                        stats.occurenceCount++;
                    }
                }


            }

            printStats(pathStatsBROAD, "PAth: broad stats");
        }

     }

    private static Map<EvaluationItem.EVALSOURCE, StatObj> getNewStatMap() {

        Map<EvaluationItem.EVALSOURCE, StatObj> statsMap = new HashMap<>();
        //Init Map
        for (EvaluationItem.EVALSOURCE source: EvaluationItem.EVALSOURCE.values()) {
            statsMap.put(source, new StatObj());
        }
        return statsMap;
    }

    private static void printStats(Map<EvaluationItem.EVALSOURCE, StatObj> statsMap, String title){
        System.out.println();
        System.out.println(title);
        for (EvaluationItem.EVALSOURCE source : statsMap.keySet()) {
            StatObj stats = statsMap.get(source);
            float stat = ((float) stats.occurenceCount / (float) stats.totalCount);
            System.out.println(source.name() + ": " + stat);
        }
    }

    private static class StatObj{
        public long totalCount = 0;
        public long occurenceCount = 0;
    }





}
