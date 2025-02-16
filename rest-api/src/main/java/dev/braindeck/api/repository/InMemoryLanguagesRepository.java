package dev.braindeck.api.repository;


import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class InMemoryLanguagesRepository {

    private final Map<Integer,String> allLanguages = Collections.synchronizedMap(new HashMap<>());
    private final Map<Integer,String> myLanguages = Collections.synchronizedMap(new HashMap<>());
    private final Map<Integer,String> topLanguages = Collections.synchronizedMap(new HashMap<>());
    private final Map<Integer,String> restLanguages = Collections.synchronizedMap(new HashMap<>());

    public InMemoryLanguagesRepository() {
        myLanguages.put(0, "English");
        myLanguages.put(1, "German");
        myLanguages.put(2, "Russian");

        topLanguages.put(3, "Spanish");
        topLanguages.put(4, "Chemistry");
        topLanguages.put(5, "Chinese (Pinyin)");
        topLanguages.put(6, "Chinese (Simplified)");
        topLanguages.put(7, "Chinese (Traditional)");
        topLanguages.put(8, "French");
        topLanguages.put(9, "Italian");
        topLanguages.put(10, "Japanese");
        topLanguages.put(11, "Japanese (Romaji)");
        topLanguages.put(12, "Korean");
        topLanguages.put(13, "Latin");
        topLanguages.put(14, "Math / Symbols");

        restLanguages.put(15, "Afrikaans");
        restLanguages.put(16, "Akan");
        restLanguages.put(17, "Akkadian");
        restLanguages.put(18, "Albanian");
        restLanguages.put(19, "American Sign Language");
        restLanguages.put(20, "Amharic");
        restLanguages.put(21, "Anglo-Saxon");
        restLanguages.put(22, "Arabic");
        restLanguages.put(23, "Armenian");
        restLanguages.put(24, "Azerbaijani");
        restLanguages.put(25, "Basque");
        restLanguages.put(26, "Belarusian");
        restLanguages.put(27, "Bengali");
        restLanguages.put(28, "Bihari");
        restLanguages.put(29, "Bosnian");
        restLanguages.put(30, "Breton");
        restLanguages.put(31, "Bulgarian");
        restLanguages.put(32, "Burmese");
        restLanguages.put(33, "Catalan");
        restLanguages.put(34, "Cebuano");
        restLanguages.put(35, "Chamorro");
        restLanguages.put(36, "Cherokee");
        restLanguages.put(37, "Choctaw");
        restLanguages.put(38, "Coptic");
        restLanguages.put(39, "Corsican");
        restLanguages.put(40, "Croatian");
        restLanguages.put(41, "Czech");
        restLanguages.put(42, "Danish");
        restLanguages.put(43, "Dene");
        restLanguages.put(44, "Dhivehi");
        restLanguages.put(45, "Dholuo");
        restLanguages.put(46, "Dutch");
        restLanguages.put(47, "Esperanto");
        restLanguages.put(48, "Estonian");
        restLanguages.put(49, "Faroese");
        restLanguages.put(50, "Filipino");
        restLanguages.put(51, "Finnish");
        restLanguages.put(52, "Fula");
        restLanguages.put(53, "Gaelic");
        restLanguages.put(54, "Galician");
        restLanguages.put(55, "Georgian");
        restLanguages.put(56, "Gothic");
        restLanguages.put(57, "Greek");
        restLanguages.put(58, "Guarani");
        restLanguages.put(59, "Gujarati");
        restLanguages.put(60, "Haida");
        restLanguages.put(61, "Haitian Creole");
        restLanguages.put(62, "Hausa");
        restLanguages.put(63, "Hawaiian");
        restLanguages.put(64, "Hebrew");
        restLanguages.put(65, "Hindi");
        restLanguages.put(66, "Hmong");
        restLanguages.put(67, "Hungarian");
        restLanguages.put(68, "Icelandic");
        restLanguages.put(69, "Igbo");
        restLanguages.put(70, "Ilonggo");
        restLanguages.put(71, "Indonesian");
        restLanguages.put(72, "International Phonetic Alphabet (IPA)");
        restLanguages.put(73, "Inuktitut");
        restLanguages.put(74, "Irish");
        restLanguages.put(75, "Javanese");
        restLanguages.put(76, "Jola-Fonyi");
        restLanguages.put(77, "Kannada");
        restLanguages.put(78, "Kazakh");
        restLanguages.put(79, "Khmer");
        restLanguages.put(80, "KiKongo");
        restLanguages.put(81, "Kinyarwanda");
        restLanguages.put(82, "Kiowa");
        restLanguages.put(83, "Konkow");
        restLanguages.put(84, "Kurdish");
        restLanguages.put(85, "Kyrgyz");
        restLanguages.put(86, "Lakota");
        restLanguages.put(87, "Lao");
        restLanguages.put(88, "Latvian");
        restLanguages.put(89, "Lenape");
        restLanguages.put(90, "Lingala");
        restLanguages.put(91, "Lithuanian");
        restLanguages.put(92, "Luba-Kasai");
        restLanguages.put(93, "Luxembourgish");
        restLanguages.put(94, "Macedonian");
        restLanguages.put(95, "Malagasy");
        restLanguages.put(96, "Malay");
        restLanguages.put(97, "Malayalam");
        restLanguages.put(98, "Maltese");
        restLanguages.put(99, "Mandinka");
        restLanguages.put(100, "Maori");
        restLanguages.put(101, "Maori (Cook Islands)");
        restLanguages.put(102, "Marathi");
        restLanguages.put(103, "Marshallese");
        restLanguages.put(104, "Mohawk");
        restLanguages.put(105, "Mongolian");
        restLanguages.put(106, "Nahuatl");
        restLanguages.put(107, "Navajo");
        restLanguages.put(108, "Nepali");
        restLanguages.put(109, "Norwegian");
        restLanguages.put(110, "Occitan");
        restLanguages.put(111, "Ojibwe");
        restLanguages.put(112, "Oriya");
        restLanguages.put(113, "Oromo");
        restLanguages.put(114, "Other / Unknown");
        restLanguages.put(115, "Pāli");
        restLanguages.put(116, "Pashto");
        restLanguages.put(117, "Persian");
        restLanguages.put(118, "Polish");
        restLanguages.put(119, "Portuguese");
        restLanguages.put(120, "Punjabi");
        restLanguages.put(121, "Quechua");
        restLanguages.put(122, "Romanian");
        restLanguages.put(123, "Romansh");
        restLanguages.put(124, "Samoan");
        restLanguages.put(125, "Sanskrit");
        restLanguages.put(126, "Seneca");
        restLanguages.put(127, "Serbian");
        restLanguages.put(128, "Sgaw Karen");
        restLanguages.put(129, "Shan");
        restLanguages.put(130, "Sindhi");
        restLanguages.put(131, "Sinhalese");
        restLanguages.put(132, "Slovak");
        restLanguages.put(133, "Slovenian");
        restLanguages.put(134, "Somali");
        restLanguages.put(135, "Sundanese");
        restLanguages.put(136, "Swahili");
        restLanguages.put(137, "Swedish");
        restLanguages.put(138, "Syriac");
        restLanguages.put(139, "Tagalog");
        restLanguages.put(140, "Tajik");
        restLanguages.put(141, "Tamil");
        restLanguages.put(142, "Tatar");
        restLanguages.put(143, "Telugu");
        restLanguages.put(144, "Tetum");
        restLanguages.put(145, "Thai");
        restLanguages.put(146, "Tibetan");
        restLanguages.put(147, "Tigrinya");
        restLanguages.put(148, "Tohono O'odham");
        restLanguages.put(149, "Tonga");
        restLanguages.put(150, "Triki");
        restLanguages.put(151, "Turkish");
        restLanguages.put(152, "Tuvan");
        restLanguages.put(153, "Uighur");
        restLanguages.put(154, "Ukrainian");
        restLanguages.put(155, "Urdu");
        restLanguages.put(156, "Uzbek");
        restLanguages.put(157, "Vietnamese");
        restLanguages.put(158, "Welsh");
        restLanguages.put(159, "Western Frisian");
        restLanguages.put(160, "Winnebago");
        restLanguages.put(161, "Wolof");
        restLanguages.put(162, "Xhosa");
        restLanguages.put(163, "Yiddish");
        restLanguages.put(164, "Yoruba");
        restLanguages.put(165, "Zulu");

        myLanguages.forEach((key, value) -> allLanguages.put(key, value));
        topLanguages.forEach((key, value) -> allLanguages.put(key, value));
        restLanguages.forEach((key, value) -> allLanguages.put(key, value));
    }

    public Map<Integer,String> findAll() {
        return Collections.unmodifiableMap(allLanguages);
    }

    public Map<Integer,String> getMy() {
        return Collections.unmodifiableMap(myLanguages);
    }

    public Map<Integer,String> getTop() {
        return Collections.unmodifiableMap(topLanguages);
    }

    public Map<Integer,String> getRest() {
        return Collections.unmodifiableMap(restLanguages);
    }

    public String getById (int id) {
        return allLanguages.get(id);
    }
}





