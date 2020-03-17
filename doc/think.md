service:
---

quando parte si auto configura tramite uno dei 'src' previsti  che gli viene passato come parametro/variabile d'ambiente

* local file
* config server (anche più di uno)
* (git?)

Una volta partito chiama periodicamente il cfg server percomunicare il suo endpoint

quindi:

* il cfg server sembra mandatorio per i servizi (a questo punto gli altri src sono inutili?

Domanda: possiamo fare a meno della resilienza? Da questo dipende l'architettura, non conviene mantenere aperte le due ipotesi contemporaneamente.

Assumiamo che la resilienza sia irrinunciabile => il config server è parte fondamentale dell'architettura







