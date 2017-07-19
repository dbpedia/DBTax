package org.dbpedia.dbtax.categories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 7/15/14 11:24 AM
 */
public class Utils {
    private static Logger logger= LoggerFactory.getLogger(Utils.class);

    public static List<String> getFileLines(String filename) {

        List<String> lines = new ArrayList<>();

        try (FileInputStream fileStream = new FileInputStream(filename);
             BufferedReader in = new BufferedReader(new InputStreamReader(fileStream, "UTF-8"))) {

            String line;

            try {
                /* Fill the existing tree */
                while ((line = in.readLine()) != null)
                    lines.add(line.trim());

            } catch (IOException e) {
                logger.error(e.getMessage());
            }

        } catch (UnsupportedEncodingException | FileNotFoundException e) {
            logger.error(e.getMessage());
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return lines;
    }
    public static <E> E getSetItem(Set<E> set, E item) {
        for (E e: set) {
            if (e.equals(item))
                return e;
        }
        return null;
    }
}