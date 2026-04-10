package models.commonModels;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Table officielle des combinaisons de finitions aux fléchettes.
 * Règles : finition obligatoire sur un double (ou Bull = D-Bull = 50 pts).
 * Scores impossibles : 1, et les scores ≥ 171 sauf 171..170.
 */
public class CheckoutTable {

    /** Scores entre 2 et 170 mathématiquement impossibles à finir en ≤ 3 fléchettes. */
    private static final int[] IMPOSSIBLE = { 163, 165, 166, 168, 169 };

    private static final Map<Integer, List<String>> TABLE = new HashMap<>();

    private static List<String> l(String... s) { return Arrays.asList(s); }

    static {
        // 1 FLECHETTE
        TABLE.put(2,   l("D1"));   TABLE.put(4,   l("D2"));   TABLE.put(6,  l("D3"));
        TABLE.put(8,   l("D4"));   TABLE.put(10,  l("D5"));   TABLE.put(12, l("D6"));
        TABLE.put(14,  l("D7"));   TABLE.put(16,  l("D8"));   TABLE.put(18, l("D9"));
        TABLE.put(20,  l("D10"));  TABLE.put(22,  l("D11"));  TABLE.put(24, l("D12"));
        TABLE.put(26,  l("D13"));  TABLE.put(28,  l("D14"));  TABLE.put(30, l("D15"));
        TABLE.put(32,  l("D16"));  TABLE.put(34,  l("D17"));  TABLE.put(36, l("D18"));
        TABLE.put(38,  l("D19"));  TABLE.put(40,  l("D20"));  TABLE.put(50, l("Bull"));

        // 2 FLECHETTES
        TABLE.put(3,  l("1","D1"));    TABLE.put(5,  l("1","D2"));    TABLE.put(7,  l("3","D2"));
        TABLE.put(9,  l("1","D4"));    TABLE.put(11, l("3","D4"));    TABLE.put(13, l("5","D4"));
        TABLE.put(15, l("7","D4"));    TABLE.put(17, l("9","D4"));    TABLE.put(19, l("3","D8"));
        TABLE.put(21, l("5","D8"));    TABLE.put(23, l("7","D8"));    TABLE.put(25, l("9","D8"));
        TABLE.put(27, l("7","D10"));   TABLE.put(29, l("9","D10"));   TABLE.put(31, l("11","D10"));
        TABLE.put(33, l("13","D10"));  TABLE.put(35, l("15","D10"));  TABLE.put(37, l("17","D10"));
        TABLE.put(39, l("7","D16"));   TABLE.put(41, l("9","D16"));   TABLE.put(43, l("11","D16"));
        TABLE.put(45, l("13","D16"));  TABLE.put(47, l("15","D16"));  TABLE.put(49, l("9","D20"));
        TABLE.put(51, l("11","D20"));  TABLE.put(53, l("13","D20"));  TABLE.put(55, l("15","D20"));
        TABLE.put(57, l("17","D20"));  TABLE.put(59, l("19","D20"));
        TABLE.put(61, l("11","D25"));  TABLE.put(62, l("12","D25"));  TABLE.put(63, l("13","D25"));
        TABLE.put(64, l("14","D25"));  TABLE.put(65, l("15","D25"));  TABLE.put(66, l("16","D25"));
        TABLE.put(67, l("17","D25"));  TABLE.put(68, l("18","D25"));  TABLE.put(69, l("19","D25"));
        TABLE.put(70, l("T10","D20")); TABLE.put(71, l("T13","D16")); TABLE.put(72, l("T12","D18"));
        TABLE.put(73, l("T11","D20")); TABLE.put(74, l("T14","D16")); TABLE.put(75, l("T15","D15"));
        TABLE.put(76, l("T16","D14")); TABLE.put(77, l("T15","D16")); TABLE.put(78, l("T18","D12"));
        TABLE.put(79, l("T13","D20")); TABLE.put(80, l("T20","D10")); TABLE.put(81, l("T15","D18"));
        TABLE.put(82, l("T14","D20")); TABLE.put(83, l("T17","D16")); TABLE.put(84, l("T20","D12"));
        TABLE.put(85, l("T15","D20")); TABLE.put(86, l("T18","D16")); TABLE.put(87, l("T17","D18"));
        TABLE.put(88, l("T16","D20")); TABLE.put(89, l("T19","D16")); TABLE.put(90, l("T18","D18"));
        TABLE.put(91, l("T17","D20")); TABLE.put(92, l("T20","D16")); TABLE.put(93, l("T19","D18"));
        TABLE.put(94, l("T18","D20")); TABLE.put(95, l("T19","D19")); TABLE.put(96, l("T20","D18"));
        TABLE.put(97, l("T19","D20")); TABLE.put(98, l("T20","D19")); TABLE.put(100,l("T20","D20"));
        TABLE.put(101,l("T17","D25")); TABLE.put(110,l("T20","Bull"));

        // 3 FLECHETTES
        TABLE.put(99,  l("T19","10","D16"));
        TABLE.put(102, l("T20","2","D20"));
        TABLE.put(103, l("T19","6","D20"));
        TABLE.put(104, l("T20","4","D20"));
        TABLE.put(105, l("T20","9","D18"));
        TABLE.put(106, l("T20","10","D18"));
        TABLE.put(107, l("T19","10","D20"));
        TABLE.put(108, l("T20","12","D18"));
        TABLE.put(109, l("T20","9","D20"));
        TABLE.put(111, l("T20","11","D20"));
        TABLE.put(112, l("T20","12","D20"));
        TABLE.put(113, l("T20","13","D20"));
        TABLE.put(114, l("T20","14","D20"));
        TABLE.put(115, l("T20","15","D20"));
        TABLE.put(116, l("T20","16","D20"));
        TABLE.put(117, l("T20","17","D20"));
        TABLE.put(118, l("T20","18","D20"));
        TABLE.put(119, l("T20","19","D20"));
        TABLE.put(120, l("T20","20","D20"));
        TABLE.put(121, l("T20","T11","D14"));
        TABLE.put(122, l("T18","T16","D14"));
        TABLE.put(123, l("T19","16","D20"));
        TABLE.put(124, l("T20","T14","D11"));
        TABLE.put(125, l("T20","T15","D10"));
        TABLE.put(126, l("T19","T19","D6"));
        TABLE.put(127, l("T20","T17","D8"));
        TABLE.put(128, l("T20","T18","D7"));
        TABLE.put(129, l("T19","T16","D12"));
        TABLE.put(130, l("T20","T18","D8"));
        TABLE.put(131, l("T20","T13","D16"));
        TABLE.put(132, l("T20","T16","D12"));
        TABLE.put(133, l("T20","T19","D8"));
        TABLE.put(134, l("T20","T14","D16"));
        TABLE.put(135, l("T20","T17","D12"));
        TABLE.put(136, l("T20","T20","D8"));
        TABLE.put(137, l("T20","T19","D10"));
        TABLE.put(138, l("T20","T18","D12"));
        TABLE.put(139, l("T20","T19","D11"));
        TABLE.put(140, l("T20","T20","D10"));
        TABLE.put(141, l("T20","T19","D12"));
        TABLE.put(142, l("T20","T20","D11"));
        TABLE.put(143, l("T20","T17","D16"));
        TABLE.put(144, l("T20","T20","D12"));
        TABLE.put(145, l("T20","T19","D14"));
        TABLE.put(146, l("T20","T18","D16"));
        TABLE.put(147, l("T20","T17","D18"));
        TABLE.put(148, l("T20","T20","D14"));
        TABLE.put(149, l("T20","T19","D16"));
        TABLE.put(150, l("T20","T18","D18"));
        TABLE.put(151, l("T20","T17","D20"));
        TABLE.put(152, l("T20","T20","D16"));
        TABLE.put(153, l("T20","T19","D18"));
        TABLE.put(154, l("T20","T18","D20"));
        TABLE.put(155, l("T20","T19","D19"));
        TABLE.put(156, l("T20","T20","D18"));
        TABLE.put(157, l("T20","T19","D20"));
        TABLE.put(158, l("T20","T20","D19"));
        TABLE.put(159, l("T20","T7","Bull"));
        TABLE.put(160, l("T20","T20","D20"));
        TABLE.put(161, l("T20","T17","Bull"));
        TABLE.put(162, l("T20","T18","Bull")); // ou T18 T18 Bull
        TABLE.put(164, l("T20","T18","Bull"));
        TABLE.put(167, l("T20","T19","Bull"));
        TABLE.put(170, l("T20","T20","Bull"));
    }

    /**
     * Retourne les suggestions de finition pour un score et un nombre de fléchettes restantes.
     * @param score  Score restant du joueur
     * @param darts  Fléchettes restantes (1, 2 ou 3)
     * @return Suggestions (ex: ["T20","T20","D20"]) ou liste vide si impossible
     */
    public static List<String> getCheckout(int score, int darts) {
        if (score < 2 || score > 170) return Collections.emptyList();
        if (score == 1)               return Collections.emptyList();
        for (int imp : IMPOSSIBLE) if (imp == score) return Collections.emptyList();

        List<String> combo = TABLE.get(score);
        if (combo == null) return Collections.emptyList();

        // Ne propose la combinaison que si on a assez de fléchettes
        return combo.size() <= darts ? combo : Collections.emptyList();
    }

    /** Indique si un score est impossible à terminer. */
    public static boolean isImpossible(int score) {
        if (score == 1 || score > 170) return true;
        for (int imp : IMPOSSIBLE) if (imp == score) return true;
        return TABLE.get(score) == null;
    }
}
