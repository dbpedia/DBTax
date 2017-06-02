package org.dbpedia.categories;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 2/23/15 10:32 PM
 */
public class WikidataLabelResolver {

    public static void main(String[] args) {

        /**
         * arg0 => labels tsv file QX\tLabel
         * arg1 => file to resolve
         * */

        Map<String, String> labels = getLabelMap(args[0]);


        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(args[1]), "UTF-8"));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1] + ".resolved"), "UTF8")))
        {
            String line = null;
            /* Fill the existing tree */
            while ((line = in.readLine()) != null) {
                String[] splitted = line.split(" ");

                for (int i = 0; i < splitted.length; i++) {
                    String value = splitted[i];
                    if (labels.containsKey(value)) {
                        value = labels.get(value);
                    }
                    writer.write(value);
                    writer.write(" ");
                }
                writer.write('\n');

            }

            writer.close();

        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File " + args[1] + " not fount!", e);

        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UnsupportedEncodingException: ", e);
        } catch (IOException e) {
            e.printStackTrace();
        }





    }

    private static Map<String, String> getLabelMap(String labelFile) {
        Map<String, String> labels = new HashMap<>();



        try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(labelFile), "UTF-8")))
        {
            String line = null;
            /* Fill the existing tree */
                while ((line = in.readLine()) != null) {
                    String[] splitted = line.split("\t");
                    if (splitted.length == 2) {
                        labels.put(splitted[0], splitted[1]);
                    }
                }


        } catch (FileNotFoundException e) {
            throw new IllegalArgumentException("File " + labelFile + " not fount!", e);

        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException("UnsupportedEncodingException: ", e);
        } catch (IOException e) {
            e.printStackTrace();
        }



        return labels;
    }



}
