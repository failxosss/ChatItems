# ChatItems

Paper plugin pro Minecraft 1.21+.

![Build](https://github.com/TVOJE-JMENO/ChatItems/actions/workflows/build.yml/badge.svg)

## Co dělá

- Napíšeš do chatu `[i]` nebo `[item]` → všem se v chatu pošle zpráva typu
  „Jméno ukazuje item [Diamond Sword x1] **[Klikni pro zobrazení]**“.
  Kdokoliv na `[Klikni pro zobrazení]` klikne, otevře se mu **normální GUI okno** (jako truhla)
  s tím itemem přesně uprostřed.
- Napíšeš `[inv]` nebo `[inventory]` → pošle se zpráva „Jméno ukazuje svůj inventář **[Klikni pro zobrazení]**“.
  Po kliknutí se otevře 6řádkové GUI, kde je hráčův inventář (3 řádky + hotbar) vycentrovaný
  uprostřed okna (nahoře a dole je prázdný řádek jako odsazení) — vypadá to přesně jako
  normální inventář, jen v truhle.
- Okno je **read-only** — nejde z něj nic vzít ani do něj nic vložit, jen se dívat (žádné
  duplikování itemů).
- Samotná zpráva `[i]`/`[inv]` se do chatu jako obyčejný text neposílá — rovnou se nahradí
  tou klikací zprávou.

Aliasy triggerů (`[i]`, `[item]`, `[inv]`, `[inventory]`) si upravíš v
`src/main/java/cz/example/chatitems/ChatListener.java` (konstanty `ITEM_TAGS`, `INV_TAGS`).

## Jak to technicky funguje

- Když někdo napíše `[i]`/`[inv]`, plugin si udělá "snapshot" itemu / inventáře a uloží ho
  do paměti (`ViewRegistry`) pod číselné ID.
- Zpráva v chatu obsahuje klikací část s `ClickEvent.runCommand("/citems item <id>")`
  (resp. `inv`), tenhle příkaz je zaregistrovaný v `plugin.yml`.
- Klikne-li hráč, spustí se u něj příkaz `/citems ...`, ten najde snapshot podle ID
  a otevře mu `Bukkit.createInventory(...)` naplněný klony itemů — je to tedy kopie,
  ne přímý přístup k reálnému inventáři majitele.
- `ChatItemsViewHolder` označuje, že dané GUI patří pluginu, a `ViewGuiListener` v takovém
  GUI ruší veškeré kliky/tažení — proto je okno jen na koukání.
- Nejstarších 500 náhledů se automaticky maže z paměti, aby plugin časem nezabíral zbytečně
  moc RAM (limit jde změnit v `ViewRegistry.MAX_ENTRIES`).

## Build

Potřebuješ JDK 21 a Maven.

```bash
cd ChatItemsPlugin
mvn clean package
```

Výsledný jar: `target/chatitems-1.0.0.jar`
(Poprvé Maven stahuje Paper API z `repo.papermc.io` — je potřeba internet.)

## Instalace na server

1. Zkopíruj `chatitems-1.0.0.jar` do `plugins/` na Paper serveru (1.21.x).
2. Restartuj / spusť server.
3. V logu: `ChatItems plugin zapnut. Triggery: [i] [item] [inv] [inventory]`

## Poznámky

- Plugin je napsaný proti **Paper API** (kvůli `AsyncChatEvent`, Adventure komponentám
  a klikacím zprávám), na čistém Spigotu fungovat nebude.
- Pokud chceš, aby zprávu v chatu viděl jen daný hráč (ne broadcast všem), stačí
  v `ChatListener.java` nahradit `Bukkit.broadcast(...)` za `player.sendMessage(...)`.
- Náhledy (`ViewRegistry`) žijí jen v paměti serveru — po restartu serveru staré klikací
  odkazy z chatu přestanou fungovat (zobrazí se hláška, že náhled už není dostupný).

## CI

Repo obsahuje GitHub Actions workflow (`.github/workflows/build.yml`), který při každém
pushi/PR na `main` projekt zbuildí a jar nahraje jako artefakt ke stažení v záložce Actions.

## Licence

[MIT](LICENSE)
