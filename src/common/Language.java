/*
 * Copyright (C) 2013 Eros Zanchetta <eros@sslmit.unibo.it>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package common;

/**
 *
 * @author Eros Zanchetta <eros@sslmit.unibo.it>
 */
public enum Language {
    
    /**
     * Language codes whose name starts with an underscore are NOT standard
     * 
     * _unspecified means that the user did not want to specify a language
     * _null        means that a language hasn't been chosen yet
     *
     */
    
//    _chi_tw ("_chi_tw", null, "zh-tw", "Chinese Taiwan"),
//    _chi_cn ("_chi_cn", null, "zh-cn", "Chinese"),
    _null ("NULL", null, "", "<select language>"),    
    _unspecified ("_unspecified", null, "zz-zz", "Unspecified"),
    aar ("aar", null, "aa", "Afar"),
    abk ("abk", null, "ab", "Abkhazian"),
    ace ("ace", null, null, "Achinese"),
    ach ("ach", null, null, "Acoli"),
    ada ("ada", null, null, "Adangme"),
    ady ("ady", null, null, "Adyghe; Adygei"),
    afa ("afa", null, null, "Afro-Asiatic languages"),
    afh ("afh", null, null, "Afrihili"),
    afr ("afr", null, "af", "Afrikaans"),
    ain ("ain", null, null, "Ainu"),
    aka ("aka", null, "ak", "Akan"),
    akk ("akk", null, null, "Akkadian"),
    alb ("alb", "sqi", "sq", "Albanian"),
    ale ("ale", null, null, "Aleut"),
    alg ("alg", null, null, "Algonquian languages"),
    alt ("alt", null, null, "Southern Altai"),
    amh ("amh", null, "am", "Amharic"),
    ang ("ang", null, null, "English, Old (ca.450-1100)"),
    anp ("anp", null, null, "Angika"),
    apa ("apa", null, null, "Apache languages"),
    ara ("ara", null, "ar", "Arabic"),
    arc ("arc", null, null, "Official Aramaic (700-300 BCE); Imperial Aramaic (700-300 BCE)"),
    arg ("arg", null, "an", "Aragonese"),
    arm ("arm", "hye", "hy", "Armenian"),
    arn ("arn", null, null, "Mapudungun; Mapuche"),
    arp ("arp", null, null, "Arapaho"),
    art ("art", null, null, "Artificial languages"),
    arw ("arw", null, null, "Arawak"),
    asm ("asm", null, "as", "Assamese"),
    ast ("ast", null, null, "Asturian; Bable; Leonese; Asturleonese"),
    ath ("ath", null, null, "Athapascan languages"),
    aus ("aus", null, null, "Australian languages"),
    ava ("ava", null, "av", "Avaric"),
    ave ("ave", null, "ae", "Avestan"),
    awa ("awa", null, null, "Awadhi"),
    aym ("aym", null, "ay", "Aymara"),
    aze ("aze", null, "az", "Azerbaijani"),
    bad ("bad", null, null, "Banda languages"),
    bai ("bai", null, null, "Bamileke languages"),
    bak ("bak", null, "ba", "Bashkir"),
    bal ("bal", null, null, "Baluchi"),
    bam ("bam", null, "bm", "Bambara"),
    ban ("ban", null, null, "Balinese"),
    baq ("baq", "eus", "eu", "Basque"),
    bas ("bas", null, null, "Basa"),
    bat ("bat", null, null, "Baltic languages"),
    bej ("bej", null, null, "Beja; Bedawiyet"),
    bel ("bel", null, "be", "Belarusian"),
    bem ("bem", null, null, "Bemba"),
    ben ("ben", null, "bn", "Bengali"),
    ber ("ber", null, null, "Berber languages"),
    bho ("bho", null, null, "Bhojpuri"),
    bih ("bih", null, "bh", "Bihari languages"),
    bik ("bik", null, null, "Bikol"),
    bin ("bin", null, null, "Bini; Edo"),
    bis ("bis", null, "bi", "Bislama"),
    bla ("bla", null, null, "Siksika"),
    bnt ("bnt", null, null, "Bantu (Other)"),
    bos ("bos", null, "bs", "Bosnian"),
    bra ("bra", null, null, "Braj"),
    bre ("bre", null, "br", "Breton"),
    btk ("btk", null, null, "Batak languages"),
    bua ("bua", null, null, "Buriat"),
    bug ("bug", null, null, "Buginese"),
    bul ("bul", null, "bg", "Bulgarian"),
    bur ("bur", "mya", "my", "Burmese"),
    byn ("byn", null, null, "Blin; Bilin"),
    cad ("cad", null, null, "Caddo"),
    cai ("cai", null, null, "Central American Indian languages"),
    car ("car", null, null, "Galibi Carib"),
    cat ("cat", null, "ca", "Catalan; Valencian"),
    cau ("cau", null, null, "Caucasian languages"),
    ceb ("ceb", null, null, "Cebuano"),
    cel ("cel", null, null, "Celtic languages"),
    cha ("cha", null, "ch", "Chamorro"),
    chb ("chb", null, null, "Chibcha"),
    che ("che", null, "ce", "Chechen"),
    chg ("chg", null, null, "Chagatai"),
    chi ("chi", "zho", "zh", "Chinese"),
    chk ("chk", null, null, "Chuukese"),
    chm ("chm", null, null, "Mari"),
    chn ("chn", null, null, "Chinook jargon"),
    cho ("cho", null, null, "Choctaw"),
    chp ("chp", null, null, "Chipewyan; Dene Suline"),
    chr ("chr", null, null, "Cherokee"),
    chu ("chu", null, "cu", "Church Slavic; Old Slavonic; Church Slavonic; Old Bulgarian; Old Church Slavonic"),
    chv ("chv", null, "cv", "Chuvash"),
    chy ("chy", null, null, "Cheyenne"),
    cmc ("cmc", null, null, "Chamic languages"),
    cop ("cop", null, null, "Coptic"),
    cor ("cor", null, "kw", "Cornish"),
    cos ("cos", null, "co", "Corsican"),
    cpe ("cpe", null, null, "Creoles and pidgins, English based"),
    cpf ("cpf", null, null, "Creoles and pidgins, French-based"),
    cpp ("cpp", null, null, "Creoles and pidgins, Portuguese-based"),
    cre ("cre", null, "cr", "Cree"),
    crh ("crh", null, null, "Crimean Tatar; Crimean Turkish"),
    crp ("crp", null, null, "Creoles and pidgins"),
    csb ("csb", null, null, "Kashubian"),
    cus ("cus", null, null, "Cushitic languages"),
    cze ("cze", "ces", "cs", "Czech"),
    dak ("dak", null, null, "Dakota"),
    dan ("dan", null, "da", "Danish"),
    dar ("dar", null, null, "Dargwa"),
    day ("day", null, null, "Land Dayak languages"),
    del ("del", null, null, "Delaware"),
    den ("den", null, null, "Slave (Athapascan)"),
    dgr ("dgr", null, null, "Dogrib"),
    din ("din", null, null, "Dinka"),
    div ("div", null, "dv", "Divehi; Dhivehi; Maldivian"),
    doi ("doi", null, null, "Dogri"),
    dra ("dra", null, null, "Dravidian languages"),
    dsb ("dsb", null, null, "Lower Sorbian"),
    dua ("dua", null, null, "Duala"),
    dum ("dum", null, null, "Dutch, Middle"),
    dut ("dut", "nld", "nl", "Dutch; Flemish"),
    dyu ("dyu", null, null, "Dyula"),
    dzo ("dzo", null, "dz", "Dzongkha"),
    efi ("efi", null, null, "Efik"),
    egy ("egy", null, null, "Egyptian (Ancient)"),
    eka ("eka", null, null, "Ekajuk"),
    elx ("elx", null, null, "Elamite"),
    eng ("eng", null, "en", "English"),
    enm ("enm", null, null, "English, Middle"),
    epo ("epo", null, "eo", "Esperanto"),
    est ("est", null, "et", "Estonian"),
    ewe ("ewe", null, "ee", "Ewe"),
    ewo ("ewo", null, null, "Ewondo"),
    fan ("fan", null, null, "Fang"),
    fao ("fao", null, "fo", "Faroese"),
    fat ("fat", null, null, "Fanti"),
    fij ("fij", null, "fj", "Fijian"),
    fil ("fil", null, null, "Filipino; Pilipino"),
    fin ("fin", null, "fi", "Finnish"),
    fiu ("fiu", null, null, "Finno-Ugrian languages"),
    fon ("fon", null, null, "Fon"),
    fre ("fre", "fra", "fr", "French"),
    frm ("frm", null, null, "French, Middle"),
    fro ("fro", null, null, "French, Old"),
    frr ("frr", null, null, "Northern Frisian"),
    frs ("frs", null, null, "Eastern Frisian"),
    fry ("fry", null, "fy", "Western Frisian"),
    ful ("ful", null, "ff", "Fulah"),
    fur ("fur", null, null, "Friulian"),
    gaa ("gaa", null, null, "Ga"),
    gay ("gay", null, null, "Gayo"),
    gba ("gba", null, null, "Gbaya"),
    gem ("gem", null, null, "Germanic languages"),
    geo ("geo", "kat", "ka", "Georgian"),
    ger ("ger", "deu", "de", "German"),
    gez ("gez", null, null, "Geez"),
    gil ("gil", null, null, "Gilbertese"),
    gla ("gla", null, "gd", "Gaelic; Scottish Gaelic"),
    gle ("gle", null, "ga", "Irish"),
    glg ("glg", null, "gl", "Galician"),
    glv ("glv", null, "gv", "Manx"),
    gmh ("gmh", null, null, "German, Middle High"),
    goh ("goh", null, null, "German, Old High"),
    gon ("gon", null, null, "Gondi"),
    gor ("gor", null, null, "Gorontalo"),
    got ("got", null, null, "Gothic"),
    grb ("grb", null, null, "Grebo"),
    grc ("grc", null, null, "Greek, Ancient"),
    gre ("gre", "ell", "el", "Greek, Modern"),
    grn ("grn", null, "gn", "Guarani"),
    gsw ("gsw", null, null, "Swiss German; Alemannic; Alsatian"),
    guj ("guj", null, "gu", "Gujarati"),
    gwi ("gwi", null, null, "Gwich'in"),
    hai ("hai", null, null, "Haida"),
    hat ("hat", null, "ht", "Haitian; Haitian Creole"),
    hau ("hau", null, "ha", "Hausa"),
    haw ("haw", null, null, "Hawaiian"),
    heb ("heb", null, "he", "Hebrew"),
    her ("her", null, "hz", "Herero"),
    hil ("hil", null, null, "Hiligaynon"),
    him ("him", null, null, "Himachali languages; Western Pahari languages"),
    hin ("hin", null, "hi", "Hindi"),
    hit ("hit", null, null, "Hittite"),
    hmn ("hmn", null, null, "Hmong; Mong"),
    hmo ("hmo", null, "ho", "Hiri Motu"),
    hrv ("hrv", null, "hr", "Croatian"),
    hsb ("hsb", null, null, "Upper Sorbian"),
    hun ("hun", null, "hu", "Hungarian"),
    hup ("hup", null, null, "Hupa"),
    iba ("iba", null, null, "Iban"),
    ibo ("ibo", null, "ig", "Igbo"),
    ice ("ice", "isl", "is", "Icelandic"),
    ido ("ido", null, "io", "Ido"),
    iii ("iii", null, "ii", "Sichuan Yi; Nuosu"),
    ijo ("ijo", null, null, "Ijo languages"),
    iku ("iku", null, "iu", "Inuktitut"),
    ile ("ile", null, "ie", "Interlingue; Occidental"),
    ilo ("ilo", null, null, "Iloko"),
    ina ("ina", null, "ia", "Interlingua"),
    inc ("inc", null, null, "Indic languages"),
    ind ("ind", null, "id", "Indonesian"),
    ine ("ine", null, null, "Indo-European languages"),
    inh ("inh", null, null, "Ingush"),
    ipk ("ipk", null, "ik", "Inupiaq"),
    ira ("ira", null, null, "Iranian languages"),
    iro ("iro", null, null, "Iroquoian languages"),
    ita ("ita", null, "it", "Italian"),
    jav ("jav", null, "jv", "Javanese"),
    jbo ("jbo", null, null, "Lojban"),
    jpn ("jpn", null, "ja", "Japanese"),
    jpr ("jpr", null, null, "Judeo-Persian"),
    jrb ("jrb", null, null, "Judeo-Arabic"),
    kaa ("kaa", null, null, "Kara-Kalpak"),
    kab ("kab", null, null, "Kabyle"),
    kac ("kac", null, null, "Kachin; Jingpho"),
    kal ("kal", null, "kl", "Kalaallisut; Greenlandic"),
    kam ("kam", null, null, "Kamba"),
    kan ("kan", null, "kn", "Kannada"),
    kar ("kar", null, null, "Karen languages"),
    kas ("kas", null, "ks", "Kashmiri"),
    kau ("kau", null, "kr", "Kanuri"),
    kaw ("kaw", null, null, "Kawi"),
    kaz ("kaz", null, "kk", "Kazakh"),
    kbd ("kbd", null, null, "Kabardian"),
    kha ("kha", null, null, "Khasi"),
    khi ("khi", null, null, "Khoisan languages"),
    khm ("khm", null, "km", "Central Khmer"),
    kho ("kho", null, null, "Khotanese; Sakan"),
    kik ("kik", null, "ki", "Kikuyu; Gikuyu"),
    kin ("kin", null, "rw", "Kinyarwanda"),
    kir ("kir", null, "ky", "Kirghiz; Kyrgyz"),
    kmb ("kmb", null, null, "Kimbundu"),
    kok ("kok", null, null, "Konkani"),
    kom ("kom", null, "kv", "Komi"),
    kon ("kon", null, "kg", "Kongo"),
    kor ("kor", null, "ko", "Korean"),
    kos ("kos", null, null, "Kosraean"),
    kpe ("kpe", null, null, "Kpelle"),
    krc ("krc", null, null, "Karachay-Balkar"),
    krl ("krl", null, null, "Karelian"),
    kro ("kro", null, null, "Kru languages"),
    kru ("kru", null, null, "Kurukh"),
    kua ("kua", null, "kj", "Kuanyama; Kwanyama"),
    kum ("kum", null, null, "Kumyk"),
    kur ("kur", null, "ku", "Kurdish"),
    kut ("kut", null, null, "Kutenai"),
    lad ("lad", null, null, "Ladino"),
    lah ("lah", null, null, "Lahnda"),
    lam ("lam", null, null, "Lamba"),
    lao ("lao", null, "lo", "Lao"),
    lat ("lat", null, "la", "Latin"),
    lav ("lav", null, "lv", "Latvian"),
    lez ("lez", null, null, "Lezghian"),
    lim ("lim", null, "li", "Limburgan; Limburger; Limburgish"),
    lin ("lin", null, "ln", "Lingala"),
    lit ("lit", null, "lt", "Lithuanian"),
    lol ("lol", null, null, "Mongo"),
    loz ("loz", null, null, "Lozi"),
    ltz ("ltz", null, "lb", "Luxembourgish; Letzeburgesch"),
    lua ("lua", null, null, "Luba-Lulua"),
    lub ("lub", null, "lu", "Luba-Katanga"),
    lug ("lug", null, "lg", "Ganda"),
    lui ("lui", null, null, "Luiseno"),
    lun ("lun", null, null, "Lunda"),
    luo ("luo", null, null, "Luo (Kenya and Tanzania)"),
    lus ("lus", null, null, "Lushai"),
    mac ("mac", "mkd", "mk", "Macedonian"),
    mad ("mad", null, null, "Madurese"),
    mag ("mag", null, null, "Magahi"),
    mah ("mah", null, "mh", "Marshallese"),
    mai ("mai", null, null, "Maithili"),
    mak ("mak", null, null, "Makasar"),
    mal ("mal", null, "ml", "Malayalam"),
    man ("man", null, null, "Mandingo"),
    mao ("mao", "mri", "mi", "Maori"),
    map ("map", null, null, "Austronesian languages"),
    mar ("mar", null, "mr", "Marathi"),
    mas ("mas", null, null, "Masai"),
    may ("may", "msa", "ms", "Malay"),
    mdf ("mdf", null, null, "Moksha"),
    mdr ("mdr", null, null, "Mandar"),
    men ("men", null, null, "Mende"),
    mga ("mga", null, null, "Irish, Middle (900-1200)"),
    mic ("mic", null, null, "Mi'kmaq; Micmac"),
    min ("min", null, null, "Minangkabau"),
    mis ("mis", null, null, "Uncoded languages"),
    mkh ("mkh", null, null, "Mon-Khmer languages"),
    mlg ("mlg", null, "mg", "Malagasy"),
    mlt ("mlt", null, "mt", "Maltese"),
    mnc ("mnc", null, null, "Manchu"),
    mni ("mni", null, null, "Manipuri"),
    mno ("mno", null, null, "Manobo languages"),
    moh ("moh", null, null, "Mohawk"),
    mon ("mon", null, "mn", "Mongolian"),
    mos ("mos", null, null, "Mossi"),
    mul ("mul", null, null, "Multiple languages"),
    mun ("mun", null, null, "Munda languages"),
    mus ("mus", null, null, "Creek"),
    mwl ("mwl", null, null, "Mirandese"),
    mwr ("mwr", null, null, "Marwari"),
    myn ("myn", null, null, "Mayan languages"),
    myv ("myv", null, null, "Erzya"),
    nah ("nah", null, null, "Nahuatl languages"),
    nai ("nai", null, null, "North American Indian languages"),
    nap ("nap", null, null, "Neapolitan"),
    nau ("nau", null, "na", "Nauru"),
    nav ("nav", null, "nv", "Navajo; Navaho"),
    nbl ("nbl", null, "nr", "Ndebele, South; South Ndebele"),
    nde ("nde", null, "nd", "Ndebele, North; North Ndebele"),
    ndo ("ndo", null, "ng", "Ndonga"),
    nds ("nds", null, null, "Low German; Low Saxon; German, Low; Saxon, Low"),
    nep ("nep", null, "ne", "Nepali"),
    nia ("nia", null, null, "Nias"),
    nic ("nic", null, null, "Niger-Kordofanian languages"),
    niu ("niu", null, null, "Niuean"),
    nno ("nno", null, "nn", "Norwegian Nynorsk; Nynorsk, Norwegian"),
    nob ("nob", null, "nb", "Bokmål, Norwegian; Norwegian Bokmål"),
    nog ("nog", null, null, "Nogai"),
    non ("non", null, null, "Norse, Old"),
    nor ("nor", null, "no", "Norwegian"),
    nqo ("nqo", null, null, "N'Ko"),
    nso ("nso", null, null, "Pedi; Sepedi; Northern Sotho"),
    nub ("nub", null, null, "Nubian languages"),
    nwc ("nwc", null, null, "Classical Newari; Old Newari; Classical Nepal Bhasa"),
    nya ("nya", null, "ny", "Chichewa; Chewa; Nyanja"),
    nym ("nym", null, null, "Nyamwezi"),
    nyn ("nyn", null, null, "Nyankole"),
    nyo ("nyo", null, null, "Nyoro"),
    nzi ("nzi", null, null, "Nzima"),
    oci ("oci", null, "oc", "Occitan (post 1500); Provençal"),
    oji ("oji", null, "oj", "Ojibwa"),
    ori ("ori", null, "or", "Oriya"),
    orm ("orm", null, "om", "Oromo"),
    osa ("osa", null, null, "Osage"),
    oss ("oss", null, "os", "Ossetian; Ossetic"),
    ota ("ota", null, null, "Turkish, Ottoman (1500-1928)"),
    oto ("oto", null, null, "Otomian languages"),
    paa ("paa", null, null, "Papuan languages"),
    pag ("pag", null, null, "Pangasinan"),
    pal ("pal", null, null, "Pahlavi"),
    pam ("pam", null, null, "Pampanga; Kapampangan"),
    pan ("pan", null, "pa", "Panjabi; Punjabi"),
    pap ("pap", null, null, "Papiamento"),
    pau ("pau", null, null, "Palauan"),
    peo ("peo", null, null, "Persian, Old (ca.600-400 B.C.)"),
    per ("per", "fas", "fa", "Persian"),
    phi ("phi", null, null, "Philippine languages"),
    phn ("phn", null, null, "Phoenician"),
    pli ("pli", null, "pi", "Pali"),
    pol ("pol", null, "pl", "Polish"),
    pon ("pon", null, null, "Pohnpeian"),
    por ("por", null, "pt", "Portuguese"),
    pra ("pra", null, null, "Prakrit languages"),
    pro ("pro", null, null, "Provençal, Old (to 1500)"),
    pus ("pus", null, "ps", "Pushto; Pashto"),
    que ("que", null, "qu", "Quechua"),
    raj ("raj", null, null, "Rajasthani"),
    rap ("rap", null, null, "Rapanui"),
    rar ("rar", null, null, "Rarotongan; Cook Islands Maori"),
    roa ("roa", null, null, "Romance languages"),
    roh ("roh", null, "rm", "Romansh"),
    rom ("rom", null, null, "Romany"),
    rum ("rum", "ron", "ro", "Romanian; Moldavian; Moldovan"),
    run ("run", null, "rn", "Rundi"),
    rup ("rup", null, null, "Aromanian; Arumanian; Macedo-Romanian"),
    rus ("rus", null, "ru", "Russian"),
    sad ("sad", null, null, "Sandawe"),
    sag ("sag", null, "sg", "Sango"),
    sah ("sah", null, null, "Yakut"),
    sai ("sai", null, null, "South American Indian (Other)"),
    sal ("sal", null, null, "Salishan languages"),
    sam ("sam", null, null, "Samaritan Aramaic"),
    san ("san", null, "sa", "Sanskrit"),
    sas ("sas", null, null, "Sasak"),
    sat ("sat", null, null, "Santali"),
    scn ("scn", null, null, "Sicilian"),
    sco ("sco", null, null, "Scots"),
    sel ("sel", null, null, "Selkup"),
    sem ("sem", null, null, "Semitic languages"),
    sga ("sga", null, null, "Irish, Old (to 900)"),
    sgn ("sgn", null, null, "Sign Languages"),
    shn ("shn", null, null, "Shan"),
    sid ("sid", null, null, "Sidamo"),
    sin ("sin", null, "si", "Sinhala; Sinhalese"),
    sio ("sio", null, null, "Siouan languages"),
    sit ("sit", null, null, "Sino-Tibetan languages"),
    sla ("sla", null, null, "Slavic languages"),
    slo ("slo", "slk", "sk", "Slovak"),
    slv ("slv", null, "sl", "Slovenian"),
    sma ("sma", null, null, "Southern Sami"),
    sme ("sme", null, "se", "Northern Sami"),
    smi ("smi", null, null, "Sami languages"),
    smj ("smj", null, null, "Lule Sami"),
    smn ("smn", null, null, "Inari Sami"),
    smo ("smo", null, "sm", "Samoan"),
    sms ("sms", null, null, "Skolt Sami"),
    sna ("sna", null, "sn", "Shona"),
    snd ("snd", null, "sd", "Sindhi"),
    snk ("snk", null, null, "Soninke"),
    sog ("sog", null, null, "Sogdian"),
    som ("som", null, "so", "Somali"),
    son ("son", null, null, "Songhai languages"),
    sot ("sot", null, "st", "Sotho, Southern"),
    spa ("spa", null, "es", "Spanish"),
    srd ("srd", null, "sc", "Sardinian"),
    srn ("srn", null, null, "Sranan Tongo"),
    srp ("srp", null, "sr", "Serbian"),
    srr ("srr", null, null, "Serer"),
    ssa ("ssa", null, null, "Nilo-Saharan languages"),
    ssw ("ssw", null, "ss", "Swati"),
    suk ("suk", null, null, "Sukuma"),
    sun ("sun", null, "su", "Sundanese"),
    sus ("sus", null, null, "Susu"),
    sux ("sux", null, null, "Sumerian"),
    swa ("swa", null, "sw", "Swahili"),
    swe ("swe", null, "sv", "Swedish"),
    syc ("syc", null, null, "Classical Syriac"),
    syr ("syr", null, null, "Syriac"),
    tah ("tah", null, "ty", "Tahitian"),
    tai ("tai", null, null, "Tai languages"),
    tam ("tam", null, "ta", "Tamil"),
    tat ("tat", null, "tt", "Tatar"),
    tel ("tel", null, "te", "Telugu"),
    tem ("tem", null, null, "Timne"),
    ter ("ter", null, null, "Tereno"),
    tet ("tet", null, null, "Tetum"),
    tgk ("tgk", null, "tg", "Tajik"),
    tgl ("tgl", null, "tl", "Tagalog"),
    tha ("tha", null, "th", "Thai"),
    tib ("tib", "bod", "bo", "Tibetan"),
    tig ("tig", null, null, "Tigre"),
    tir ("tir", null, "ti", "Tigrinya"),
    tiv ("tiv", null, null, "Tiv"),
    tkl ("tkl", null, null, "Tokelau"),
    tlh ("tlh", null, null, "Klingon; tlhIngan-Hol"),
    tli ("tli", null, null, "Tlingit"),
    tmh ("tmh", null, null, "Tamashek"),
    tog ("tog", null, null, "Tonga (Nyasa)"),
    ton ("ton", null, "to", "Tonga (Tonga Islands)"),
    tpi ("tpi", null, null, "Tok Pisin"),
    tsi ("tsi", null, null, "Tsimshian"),
    tsn ("tsn", null, "tn", "Tswana"),
    tso ("tso", null, "ts", "Tsonga"),
    tuk ("tuk", null, "tk", "Turkmen"),
    tum ("tum", null, null, "Tumbuka"),
    tup ("tup", null, null, "Tupi languages"),
    tur ("tur", null, "tr", "Turkish"),
    tut ("tut", null, null, "Altaic languages"),
    tvl ("tvl", null, null, "Tuvalu"),
    twi ("twi", null, "tw", "Twi"),
    tyv ("tyv", null, null, "Tuvinian"),
    udm ("udm", null, null, "Udmurt"),
    uga ("uga", null, null, "Ugaritic"),
    uig ("uig", null, "ug", "Uighur; Uyghur"),
    ukr ("ukr", null, "uk", "Ukrainian"),
    umb ("umb", null, null, "Umbundu"),
    und ("und", null, null, "Undetermined"),
    urd ("urd", null, "ur", "Urdu"),
    uzb ("uzb", null, "uz", "Uzbek"),
    vai ("vai", null, null, "Vai"),
    ven ("ven", null, "ve", "Venda"),
    vie ("vie", null, "vi", "Vietnamese"),
    vol ("vol", null, "vo", "Volapük"),
    vot ("vot", null, null, "Votic"),
    wak ("wak", null, null, "Wakashan languages"),
    wal ("wal", null, null, "Walamo"),
    war ("war", null, null, "Waray"),
    was ("was", null, null, "Washo"),
    wel ("wel", "cym", "cy", "Welsh"),
    wen ("wen", null, null, "Sorbian languages"),
    wln ("wln", null, "wa", "Walloon"),
    wol ("wol", null, "wo", "Wolof"),
    xal ("xal", null, null, "Kalmyk; Oirat"),
    xho ("xho", null, "xh", "Xhosa"),
    yao ("yao", null, null, "Yao"),
    yap ("yap", null, null, "Yapese"),
    yid ("yid", null, "yi", "Yiddish"),
    yor ("yor", null, "yo", "Yoruba"),
    ypk ("ypk", null, null, "Yupik languages"),
    zap ("zap", null, null, "Zapotec"),
    zbl ("zbl", null, null, "Blissymbols; Blissymbolics; Bliss"),
    zen ("zen", null, null, "Zenaga"),
    zha ("zha", null, "za", "Zhuang; Chuang"),
    znd ("znd", null, null, "Zande languages"),
    zul ("zul", null, "zu", "Zulu"),
    zun ("zun", null, null, "Zuni"),
    zxx ("zxx", null, null, "No linguistic content; Not applicable"),
    zza ("zza", null, null, "Zaza; Dimili; Dimli; Kirdki; Kirmanjki; Zazaki");

    private String iso_639_2_B;
    private String iso_639_2_T;
    private String iso_639_1;    
    private String name;
    
    Language (String iso_639_2_B, String iso_639_2_T, String iso_639_1, String name) {
        this.iso_639_2_B = iso_639_2_B;
        this.iso_639_2_T = iso_639_2_T;
        this.iso_639_1   = iso_639_1;
        this.name        = name;
    }

    /**
     * 
     * @return the Iso_639_1 code of the language (2-letter code)
     */
    public String getIso_639_1() {
        return iso_639_1;
    }

    /**
     * 
     * @return the Iso_639_2_B code of the language (3-letter code)
     */
    public String getIso_639_2_B() {
        return iso_639_2_B;
    }
    
    /**
     * @return the Iso_639_2_T code of the language (3-letter code), basically useless
     */
    @Deprecated
    public String getIso_639_2_T() {
        return iso_639_2_T;
    }

    /**
     * 
     * @return the name of the language
     */
    public String getName() {
        return name;
    }

    public void setIso_639_1(String iso_639_1) {
        this.iso_639_1 = iso_639_1;
    }

    public void setIso_639_2_B(String iso_639_2_B) {
        this.iso_639_2_B = iso_639_2_B;
    }

    public void setIso_639_2_T(String iso_639_2_T) {
        this.iso_639_2_T = iso_639_2_T;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return getName();
    }
}
