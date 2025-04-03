# Cvičení 08 - IO

Vaším cílem je analyzovat a upravit projekt generické souborové databáze. Databáze slouží k persistenci (trvalému uložení) dat v souborech. Obecně lze uchovávat libovolná data, pokud splní předepsané náležitosti. V terminologii vytvořené knihovny je databázovou entitou jedna uživatelem definovaná třída, která realizuje rozhraní `DatabaseEntity`.

- rozhraní `DatabaseEntity`
  - rozhraní předepisující podobu databázové entity
  - entita musí definovat (primární) klíč, který slouží k její identifikaci
    - databáze staví na abstraktní struktuře zvaná tabulka (resp. mapa, asociativní kontejner, slovník, dictionary)
    - pro tabulku platí, že data jsou identifikována klíčem (může se jednat o dvojici klíč-hodnota nebo může být klíč součástí třídy)
    - většina operací, pak přistupuje k datům podle klíče (a nikoliv podle pořadí v poli jako u ArrayListu)
    - tabulka také typicky předpokládá, že pro jeden klíč existuje pouze jeden (unikátní) objekt, nelze tak vložit více objektů se stejným klíčem
  - klíč může být reprezentován obecně libovolným typem splňujícím `PrimaryKey` rozhraní
  - 

- rozhraní `EntityRepository`
  - předepisuje rozhraní pro repozitář specifického typu
    - podobně jako `List<User>` je seznam uživatelů
    - tak `EntityRepository<User, StringPrimaryKey>` je repozitář (databáze) uživatelů s řetězcovým (primárním) klíčem
  - rozhraní definuje operace pro vkládání, úpravu, mazání a hledání záznamů (CRUD operace - Create, Read, Update, Delete)
  - rozhraní je realizováno třídou `FileEntityRepository`
    - tato třída ukládá každý objekt jako samostatný soubor ve složce
    - aby věděl, jak uložit nebo načíst objekt do souboru, tak používá `EntitySerializer`

- abstraktní třída `TextSerializer`
  - slouží k jednodušší realizaci serializace/deserializace databázových entit

Nad daty je možné vytvářet indexy, které umožňují efektivněji vyhledat data dle specifického klíče. 

- třída `IndexedFileEntityRepository`
  - rozšiřuje souborový repozitář o podporu indexů
  - nabízí navíc metodu `findByIndexedValue` pro prohledání indexu
  - dále využívá samotné indexy (`Index`), které spravuje `IndexManager` a může se jednat o indexy
    - `UniqueIndex` - unikátní index (podobně jako primární klíč) zaručuje, že existuje jediný objekt v repozitáři se specifickou hodnotou sledovaného atributu
    - `NonUniqueIndex` - neunikátní index (může existovat více objektů se stejnou hodnotou sledovaného atributu)
    - `MultiColumnIndex` - více-atributový index (sleduje najednou několik atributů), může být unikátní i neunikátní

V balíčku `cz.upce.boop.ex` je připraena entity `User` a ukázkový `main` pracující s repozitářem těchto entit.

## Úkoly

1. Projďete a analyzujte stávající kód databáze
  - vytvořte UML diagram (nebo více diagramů), kde bude vidět struktura kódu databáze
    - nemusí být zaznamenány všechny detaily, ale musí být jasně identifikovatelné třídy, jejich předci a realizovaná rozhraní a asociační vazby mezi nimi

2. Analyzujte souborový a indexovaný repozitář
  - výsledky zaznamenejte do souboru ANALYZA.TXT
  - analyzujte souborový repozitář a poznamenejte do souboru, jaké třídy se používají pro čtení/zápis objektů z/do souborů 
  - analyzujte indexovaný repozitář a poznamenejte do souboru, jaké třídy se používají pro čtení/zápis indexů

3. Doplňte souborové logování do `Logger` (balíček `cz.upce.boop.ex.logger`)
   1. Vytvořte `LogMessageHandler` pro textový výstup   
      - vytvořte handler, který bude zapisovat logované události do textově čitelného souboru
      - jeden log záznam = jeden řádek textu, vhodně jej formátujte, aby to bylo snadno čitelné
      - nezapomeňte na korektní uzavírání souboru
   2. Doplňte podporu pro logování do `FileEntityRepository`
      - vytvořte atribut `Logger` a vhodným způsobem jej inicializujte
      - logujte veškeré základní operace z rozhraní `EntityRepository`
      - logujte chyby (výjimky)
      - doplňte logování rovněž do třídy `IndexedFileEntityRepository`
   3. Upravte `main` a doplňte logování
      - doplňte logování do souboru pro všechny události v repozitáři
      - vyzkoušejte, že to funguje a vzniká soubor obsahující všechny události
      - nezapomeňte na nutnost soubor uzavřít!
   4. Vytvořte `LogMessageHandler` pro binární výstup
      - vytvořte obdobný logovací handler, který bude vytvářet log v binárním formátu s využitím `DataOutputStream`
      - binární záznam musí obsahovat všechny atributy z `LogMessage`
      - doplňte jej do loggeru použitého v mainu a vyzkoušejte funkčnost
   5. Vytvořte třídu `BinaryLogInputStream`
      - třída slouží k načtení a dekódování `LogMessage` uložených předchozím handlerem
      - třída definuje konstruktor `BinaryLogInputStream(InputStream is)`
        - parametr slouží jako zdroj binárních dat
        - nad ním si třída vytvoří objekt `DataInputStream`
      - třída realizuje rozhraní `AutoCloseable`
        - v rámci metody `close` jsou uzavřeny streamy, které si třída sama vytvořila
      - třída definuje metodu `LogMessage readLogMessage()`
        - metoda vrací `null`, pokud již neexistuje další `LogMessage` záznam v souboru
        - metoda vrací jeden `LogMessage`, pokud je k dispozici
        - metoda vyvolává/rozšiřuje výjimku `IOException`, pokud dojde k neočekávané chybě
      - na konec `main` doplňte použití této třídy, načtěte a vypište do konzole všechny zaznamenané události v binárním log souboru

