# JackParser


# Intro
5. hovedforløb på datatekniker uddannelsen.
Dette forløb gik ud på at få en dybere forståelse for hvad der sker på det laveste niveau 
i, og under, compilering og afvikling af programmer.
Derfor startede forløbet med et forløb hvor vi byggede vores egne gates med hjælp 
fra nand2tetris (https://www.nand2tetris.org). Derefter blev der programmeret i et assembly-
lignende sprog, for at få forståelsen for hvordan data gemmes på hardware niveau. 

# Compilers
 - Spoiler: 
 Jeg havde på skolepraktikken siddet i C++, og havde derfor valgt at bygge mine compilers i 
 C++. Dette ændres da jeg kommer op i højniveau sproget 'Jack', som er et Java-ligende sprog.
 Det er grundet at min viden var begrænset og tiden var knap, så for at få adgang til data 
 collections skifter jeg til Java under 'JackParser'.

#ASMParser 
- https://github.com/chlister/ASMParser

  Denne compiler tager assembly koden og compilere det til maskin kode som kan bruges af den
  VM som Nand2Tetris stiller til rådighed.

#VMParser 
- https://github.com/chlister/VMParser 

  Denne compiler skulle emulere en slags JVM, den tager VM kode og compiler til assembly 
  kode. Grundet tidspres, er denne ikke fuldt færdig. 

#JackParser 
- https://github.com/chlister/JackParser

  Denne compiler skulle oversætte Jack kode til VM kode.
  
For alle compilers gælder det at der er et fast flow som programmet skal følge. Alt i alt 
har forløbet været meget krævende, da der var mange ting som skulle gå op. Jeg er stolt over
de ting der er lavet, men der mangler kommentarer på de første to projekter. 
Er der noget spørgsmål så send gerne en mail.
