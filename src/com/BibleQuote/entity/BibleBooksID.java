package com.BibleQuote.entity;

import java.util.ArrayList;
import java.util.HashMap;

//import android.util.Log;

public class BibleBooksID {

	//private final static String TAG = "BibleBooksID";

	private static HashMap<String, String> qualifier;
	private static HashMap<String, String[]> bookShortNames = new HashMap<String, String[]>() {
		private static final long serialVersionUID = 1L;

		{

			// Old Testament* (39)

			put("Gen", new String[]{"Быт", "Бт", "Бытие", "Ge", "Gen", "Gn", "Genesis"});
			put("Exod", new String[]{"Исх", "Исход", "Ex", "Exo", "Exod", "Exodus"});
			put("Lev", new String[]{"Лев", "Лв", "Левит", "Lev", "Le", "Lv", "Levit", "Leviticus"});
			put("Num", new String[]{"Чис", "Чс", "Числ", "Числа", "Nu", "Num", "Nm", "Numb", "Numbers"});
			put("Deut", new String[]{"Втор", "Вт", "Втрзк", "Второзаконие", "De", "Deut", "Deu", "Dt", "", "Deuteron", "Deuteronomy"});
			put("Josh", new String[]{"ИисНав", "Нав", "Иисус", "Навин", "Jos", "Josh", "Joshua"});
			put("Judg", new String[]{"Суд", "Сд", "Судьи", "Jdg", "Judg", "Judge", "Judges"});
			put("Ruth", new String[]{"Руф", "Рф", "Руфь", "Ru", "Rut", "Ruth", "Rth", "Rt"});
			put("1Sam", new String[]{"1Цар", "1Цр", "1Ц", "1Царств", "1Sa", "1S", "1Sam", "1Sm", "1Sml", "1Samuel"});
			put("2Sam", new String[]{"2Цар", "2Цр", "2Ц", "2Царств", "2Sa", "2S", "2Sam", "2Sm", "2Sml", "2Samuel"});
			put("1Kgs", new String[]{"3Цар", "3Цр", "3Ц", "3Царств", "1Ki", "1K", "1Kn", "1Kg", "1King", "1Kng", "1Kings"});
			put("2Kgs", new String[]{"4Цар", "4Цр", "4Ц", "4Царств", "2Ki", "2K", "2Kn", "2Kg", "2King", "2Kng", "2Kings"});
			put("1Chr", new String[]{"1Пар", "1Пр", "1Chr", "1Ch", "1Chron"});
			put("2Chr", new String[]{"2Пар", "2Пр", "2Chr", "2Ch", "2Chron"});
			put("Ezra", new String[]{"Ездр", "Езд", "Ез", "Ездра", "Ezr", "Ezra"});
			put("Neh", new String[]{"Нм", "Неемия", "Ne", "Neh", "Nehem", "Nehemiah"});
			put("Esth", new String[]{"Есф", "Ес", "Есфирь", "Esth", "Es", "Est", "Esther"});
			put("Job", new String[]{"Иов", "Ив", "Job", "Jb"});
			put("Ps", new String[]{"Пс", "Псалт", "Псал", "Псл", "Псалом", "Псалтирь", "Псалмы", "Ps", "Psa", "Psal", "Psalm", "Psalms"});
			put("Prov", new String[]{"Прит", "Притч", "Пр", "Притчи", "Притча", "Pr", "Prov", "Pro", "Proverb", "Proverbs"});
			put("Eccl", new String[]{"Еккл", "Ек", "Екк", "Екл", "Екклесиаст", "Ec", "Eccl", "Ecc", "Ecclesia"});
			put("Song", new String[]{"Песн", "Пес", "Псн", "Песн.Песней", "Песни", "Song", "Songs", "SS", "Sol"});
			put("Isa", new String[]{"Ис", "Иса", "Исаия", "Исайя", "Isa", "Is", "Isaiah"});
			put("Jer", new String[]{"Иер", "Иерем", "Иеремия", "Je", "Jer", "Jerem", "Jeremiah"});
			put("Lam", new String[]{"Плач", "Плч", "Пл", "Пл.Иер.", "Пл.Иер", "Плач", "Иеремии", "La", "Lam", "Lament", "Lamentation", "Lamentations"});
			put("Ezek", new String[]{"Иез", "Из", "Иезек", "Иезекииль", "Ez", "Eze", "Ezek", "Ezekiel"});
			put("Dan", new String[]{"Дан", "Дн", "Днл", "Даниил", "Da", "Dan", "Daniel"});
			put("Hos", new String[]{"Ос", "Осия", "Hos", "Ho", "Hosea"});
			put("Joel", new String[]{"Иоил", "Ил", "Иоиль", "Joel", "Joe"});
			put("Amos", new String[]{"Ам", "Амс", "Амос", "Am", "Amos", "Amo"});
			put("Obad", new String[]{"Авд", "Авдий", "Ob", "Obadiah", "Oba"});
			put("Jonah", new String[]{"Иона", "Jon", "Jona", "Jonah"});
			put("Mic", new String[]{"Мих", "Мх", "Михей", "Mi", "Mic", "Micah"});
			put("Nah", new String[]{"Наум", "Na", "Nah", "Nahum"});
			put("Hab", new String[]{"Авв", "Аввак", "Аввакум", "Hab", "Habak", "Habakkuk"});
			put("Zeph", new String[]{"Соф", "Софон", "Софония", "Zeph", "", "Zep", "Zephaniah"});
			put("Hag", new String[]{"Агг", "Аггей", "Hag", "Haggai"});
			put("Zech", new String[]{"Зах", "Зхр", "Захар", "Захария", "Ze", "Zec", "Zech", "Zechariah"});
			put("Mal", new String[]{"Мал", "Малах", "Млх", "Малахия", "Mal", "Malachi"});

			// New  Testament* (27)

			put("Matt", new String[]{"Матф", "Мтф", "Мф", "Мт", "Матфея", "Матфей", "Мат", "Mt", "Ma", "Matt", "Mat", "Matthew"});
			put("Mark", new String[]{"Мар", "Марк", "Мрк", "Мр", "Марка", "Мк", "Mk", "Mar", "Mr", "Mrk", "Mark"});
			put("Luke", new String[]{"Лук", "Лк", "Лукa", "Луки", "Lk", "Lu", "Luk", "Luke"});
			put("John", new String[]{"Иоан", "Ин", "Иоанн", "Иоанна", "Jn", "Jno", "Joh", "John"});
			put("Acts", new String[]{"Деян", "Дея", "Д.А.", "Деяния", "Ac", "Act", "Acts"});
			put("Rom", new String[]{"Рим", "Римл", "Римлянам", "Ro", "Rom", "Romans"});
			put("1Cor", new String[]{"1Кор", "1Коринф", "1Коринфянам", "1Коринфянам", "1Co", "1Cor", "1Corinth", "1Corinthians"});
			put("2Cor", new String[]{"2Кор", "2Коринф", "2Коринфянам", "2Коринфянам", "2Co", "2Cor", "2Corinth", "2Corinthians"});
			put("Gal", new String[]{"Гал", "Галат", "Галатам", "Ga", "Gal", "Galat", "Galatians"});
			put("Eph", new String[]{"Еф", "Ефес", "Ефесянам", "Eph", "Ep", "Ephes", "Ephesians"});
			put("Phil", new String[]{"Фил", "Флп", "Филип", "Филиппийцам", "Php", "Ph", "Phil", "Philip", "Philippians"});
			put("Col", new String[]{"Кол", "Колос", "Колоссянам", "Col", "Colos", "Colossians"});
			put("1Thess", new String[]{"1Фесс", "1Фес", "1Фессалоникийцам", "1Сол", "1Солунянам", "1Th", "1Thes", "1Thess", "1Thessalonians"});
			put("2Thess", new String[]{"2Фесс", "2Фес", "2Фессалоникийцам", "2Сол", "2Солунянам", "2Th", "2Thes", "2Thess", "2Thessalonians"});
			put("1Tim", new String[]{"1Тим", "", "1Тимоф", "1Тимофею", "1Ti", "1Tim", "1Timothy"});
			put("2Tim", new String[]{"2Тим", "2Тимоф", "2Тимофею", "2Ti", "2Tim", "2Timothy"});
			put("Titus", new String[]{"Тит", "Титу", "Tit", "Ti", "Titus"});
			put("Phlm", new String[]{"Флм", "Филимон", "Филимону", "Phm", "Phile", "Phlm", "Philemon"});
			put("Heb", new String[]{"Евр", "Евреям", "He", "Heb", "Hebr", "Hebrews"});
			put("Jas", new String[]{"Иак", "Ик", "Иаков", "Иакова", "Jas", "Ja", "Jam", "Jms", "James"});
			put("1Pet", new String[]{"1Пет", "1Пт", "1Птр", "1Петр", "1Петра", "1Pe", "1Pet", "1Peter"});
			put("2Pet", new String[]{"2Пет", "2Пт", "2Птр", "2Петр", "2Петра", "2Pe", "2Pet", "2Peter"});
			put("1John", new String[]{"1Иоан", "1Ин", "1Иоанн", "1Иоанна", "1Jn", "1Jo", "1Joh", "1Jno", "1John"});
			put("2John", new String[]{"2Иоан", "2Ин", "2Иоанн", "2Иоанна", "2Jn", "2Jo", "2Joh", "2Jno", "2John"});
			put("3John", new String[]{"3Иоан", "3Ин", "3Иоанн", "3Иоанна", "3Jn", "3Jo", "3Joh", "3Jno", "3John"});
			put("Jude", new String[]{"Иуд", "Ид", "Иуда", "Иуды", "Jud", "Jude", "Jd"});
			put("Rev", new String[]{"Откр", "Отк", "От", "Откровен", "Апок", "Откровение", "Апокалипсис", "Rev", "Re", "Rv", "Revelation"});

			// Apocrypha (18)

			put("Tob", new String[]{"Tob", "Tobit", "Тов", "Товит"});
			put("Jdt", new String[]{"Jdt", "Иудиф", "Иудифь", "Judith"});
			put("AddEsth", new String[]{"AddEsth", "Additions to Esther"});
			put("Wis", new String[]{"Wis", "Прем.Сол.", "Премудр.Соломон", "Премудр.Сол", "Премудр.Соломона", "Премудрость", "Премудрости", "Wisdom", "Wisdom of Solomon"});
			put("Sir", new String[]{"Sir", "Сир", "Сирах", "Sirach"});
			put("Bar", new String[]{"Bar", "Вар", "Варух", "Baruch"});
			put("EpJer", new String[]{"EpJer", "Epistle", "Посл.Иер", "Посл.Иерем", "Посл.Иеремии", "Letter of Jeremiah", "Epistle of Jeremiah"});
			put("PrAzar", new String[]{"PrAzar", "Azar", "Azariah", "Prayer of Azariah"});
			put("Sus", new String[]{"Sus", "Susanna"});
			put("Bel", new String[]{"Bel", "Bel and the Dragon"});
			put("1Macc", new String[]{"1Macc", "1Mac", "1Макк", "1Маккав", "1Мак", "1Маккавейская", "1 Maccabees "});
			put("2Macc", new String[]{"2Macc", "2Mac", "2Макк", "2Маккав", "2Мак", "2Маккавейская", "2 Maccabees "});
			put("3Macc", new String[]{"3Macc", "3Mac", "3Макк", "3Маккав", "3Мак", "3Маккавейская", "3 Maccabees "});
			put("4Macc", new String[]{"4Macc", "4Mac", "4Макк", "4Маккав", "4Мак", "4Маккавейская", "4 Maccabees "});
			put("PrMan", new String[]{"PrMan", "Manas", "Manasseh", "Prayer of Manasseh"});
			put("1Esd", new String[]{"1Esd", "1Es", "1Ездр", "1Езд", "1Ездра", "1Ездры", "1Ез", "1 Esdras"});
			put("2Esd", new String[]{"2Esd", "2Es", "2Ездр", "2Езд", "2Ездра", "2Ездры", "2Ез", "2 Esdras"});
			put("Ps151", new String[]{"Ps151", "Psalm 151", ""});

			// Rahlfs' LXX* (2)

			put("Odes", new String[]{"Odes"});
			put("PssSol", new String[]{"PssSol", "Psalms of Solomon"});

			// Vulgate & other later Latin mss* (4)

			put("EpLao", new String[]{"EpLao", ""});
			put("3Esd", new String[]{"3Esd", "3Ездр", "3Езд", "3Ездра", "3Ездры", "3Ез", "3 Esdras"});
			put("4Esd", new String[]{"4Esd", "4Ездр", "4Езд", "4Ездра", "4Ездры", "4Ез", "4 Esdras"});
			put("5Esd", new String[]{"5Esd", "5Ездр", "5Езд", "5Ездра", "5Ездры", "5Ез", "5 Esdras"});

			// Ethiopian Orthodox Canon/Ge'ez Translation Additions (6)

			put("1En", new String[]{"1En", "1 Enoch"});
			put("Jub", new String[]{"Jub", "Jubilees"});
			put("4Bar", new String[]{"4Bar", "4 Baruch", "Paraleipomena Jeremiou"});
			put("AscenIsa", new String[]{"AscenIsa", "Ascension of Isaiah"});
			put("PsJos", new String[]{"PsJos", "Pseudo-Josephus"});

			// Coptic Orthodox Canon Additions (3)

			put("AposCon", new String[]{"AposCon", "Apostolic Constitutions and Canons"});
			put("1Clem", new String[]{"1Clem", "1 Clement"});
			put("2Clem", new String[]{"2Clem", "2 Clement"});

			// Armenian Orthodox Canon Additions (4/16)

			put("3Cor", new String[]{"3Cor", "3 Corinthians"});
			put("EpCorPaul", new String[]{"EpCorPaul", "Epistle of the Corinthians to Paul and His Response"});
			put("JosAsen", new String[]{"JosAsen", "Joseph and Asenath"});
			put("T12Patr", new String[]{"T12Patr", "Testaments of the Twelve Patriarchs"});
			put("T12Patr.TAsh", new String[]{"T12Patr.TAsh", "Testaments of Asher"});
			put("T12Patr.TBenj", new String[]{"T12Patr.TBenj", "Testaments of Benjamin"});
			put("T12Patr.TDan", new String[]{"T12Patr.TDan", "Testaments of Dan"});
			put("T12Patr.TGad", new String[]{"T12Patr.TGad", "Testaments of Gad"});
			put("T12Patr.TIss", new String[]{"T12Patr.TIss", "Testaments of Issachar"});
			put("T12Patr.TJos", new String[]{"T12Patr.TJos", "Testaments of Joseph"});
			put("T12Patr.TJud", new String[]{"T12Patr.TJud", "Testaments of Judah"});
			put("T12Patr.TLevi", new String[]{"T12Patr.TLevi", "Testaments of Levi"});
			put("T12Patr.TNaph", new String[]{"T12Patr.TNaph", "Testaments of Naphtali"});
			put("T12Patr.TReu", new String[]{"T12Patr.TReu", "Testaments of Reuben"});
			put("T12Patr.TSim", new String[]{"T12Patr.TSim", "Testaments of Simeon"});
			put("T12Patr.TZeb", new String[]{"T12Patr.TZeb", "Testaments of Zebulun"});

			// Peshitta (2)

			put("2Bar", new String[]{"2Bar", "2 Baruch"});
			put("EpBar", new String[]{"EpBar", "Letter of Baruch"});

			// Codex Sinaiticus (2/5)

			put("Barn", new String[]{"Barn", "Barnabas"});
			put("Herm", new String[]{"Herm", "Shepherd of Hermas"});
			put("Herm.Mand", new String[]{"Herm.Mand", "Shepherd of Hermas, Mandates"});
			put("Herm.Sim", new String[]{"Herm.Sim", "Shepherd of Hermas, Similitudes"});
			put("Herm.Vis", new String[]{"Herm.Vis", "Shepherd of Hermas, Visions"});
		}
	};

	private static void qualifierInit() {
		qualifier = new HashMap<String, String>();
		for (String key : bookShortNames.keySet()) {
			String[] bookNames = bookShortNames.get(key);
			for (String name : bookNames) {
				qualifier.put(name.toLowerCase(), key);
			}
		}
	}

	;

	private static void addBookID(String id, ArrayList<String> shortNames) {
		for (String name : shortNames) {
			qualifier.put(name.toLowerCase(), id);
		}
	}

	public static String getID(String shortNames) {
		ArrayList<String> moduleShortNames = new ArrayList<String>();
		for (String shName : shortNames.split(" ")) moduleShortNames.add(shName);
		return getID(moduleShortNames);
	}

	public static String getID(ArrayList<String> shortNames) {
		String result = null;

		if (shortNames.size() == 0) {
			return result;
		}

		if (qualifier == null) {
			qualifierInit();
		}

		for (String moduleBookName : shortNames) {
			result = qualifier.get(moduleBookName.toLowerCase());
			if (result != null) {
				break;
			}
		}

		if (result != null) {
			addBookID(result, shortNames);
		} else {
			addBookID(shortNames.get(0), shortNames);
		}

		return result;
	}
}
