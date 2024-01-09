import java.util.Comparator;
import java.util.Map;


/**
 * Class to output a Compare Strings.
 *
 * @author Rahul Rajaram
 */
public class IntCompareCount implements Comparator<Map.Entry<String, Integer>> {

    /**
     * Compare method for Strings.
     *
     * @param o1
     *            First String
     *
     * @param o2
     *            Second String
     * @return compareTo value
     *
     * @requires o1, o2 not null
     * @ensures that compare >0 if o1 is alphabetically greater than o2, compare
     *          less than 0 if opposite, and compare=0 if o1=o2
     */
    @Override
    public int compare(Map.Entry<String, Integer> p1, Map.Entry<String, Integer> p2) {
        return p2.getValue().compareTo(p1.getValue());
    }

}
