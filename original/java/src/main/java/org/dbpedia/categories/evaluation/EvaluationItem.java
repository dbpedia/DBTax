package org.dbpedia.categories.evaluation;

/**
 * Description
 *
 * @author Dimitris Kontokostas
 * @since 7/15/14 10:41 AM
 */
public class EvaluationItem <E> {
    public String getEvalID() {
        return evalID;
    }

    public String getName() {
        return name;
    }

    public EVALTYPE getType() {
        return type;
    }

    public EVALSOURCE getSource() {
        return source;
    }
    public int getPos() {
        return position;
    }

    public enum EVALTYPE { CLASS, PATH }
    public enum EVALSOURCE {DBPEDIA, YAGO, WIKIPEDIA, DBTAX, WIBI, WIKIDATA}

    private final String evalID;
    private final String name;
    private final int position;
    private final EVALTYPE type;
    private final EVALSOURCE source;




    public EvaluationItem(String evalID, String name) {
        this.evalID = evalID.toLowerCase().replace("ci", "c");
        this.name = name;

        Character p = this.evalID.toLowerCase().charAt(0);
        switch (p) {
            case 'p': this.type = EVALTYPE.PATH; break;
            case 'c': this.type = EVALTYPE.CLASS; break;
            default: throw new IllegalArgumentException("Invalid source type: " + evalID);
        }

        int offset = 0;

        Character c = this.evalID.toLowerCase().charAt(1);
        switch (c) {
            case 'd': this.source = EVALSOURCE.DBPEDIA; offset = 0; break;
            case 'y': this.source = EVALSOURCE.YAGO; offset = 50; break;
            case 's': this.source = EVALSOURCE.WIKIPEDIA; offset = 100; break;
            case 'c': this.source = EVALSOURCE.DBTAX; offset = 150; break;
            case 'b': this.source = EVALSOURCE.WIBI; offset = 200; break;
            case 't': this.source = EVALSOURCE.WIKIDATA; offset = 250; break;
            default: throw new IllegalArgumentException("Invalid source type: " + evalID);
        }




        position = offset + Integer.parseInt(this.evalID.substring(2));

    }



    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EvaluationItem)) return false;

        EvaluationItem that = (EvaluationItem) o;

        if (!evalID.equals(that.evalID)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return evalID.hashCode();
    }
}
