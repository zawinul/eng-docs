LINUX VM
---

### Installazione Linux

* scaricato ISO di installazione da 
https://gemmei.ftp.acc.umu.se/debian-cd/current-live/amd64/iso-hybrid/debian-live-10.4.0-amd64-kde.iso


* creato disco (min 20 giga) 
* fatto partire da cd come live
* una volta entrato nella live installato fatta partire installazione con configurazioni abbastanza intuitive
* durante l'installazione creato l'utente paolo
* settaggi locale inglese e keyboard italiana

### post installazione

* update
		sudo apt-get update

* installato docker pressappoco come in https://docs.docker.com/engine/install/debian/
* sembra che git fosse già installato
* settaggi virtualbox per directory condivisa con host e clipboard



* creato utente paolo

	sudo groupadd developers

	sudo usermod -a -G developers paolo
	sudo usermod -aG sudo paolo
	#sudo usermod -aG docker paolo

	#la shell dovrebbe già essere bash ma per sicurezza
	sudo chsh -s /bin/bash paolo
	
	git config --global user.name "Nome Cognome"
	git config --global user.email "indirizzo@mail.com"


* modificato .bashrc

		export ENGMSDIR=/var/eng-ms
		export PATH=$PATH:$ENGMSDIR/script/linux
		export MYGITUSER=******
		export MYGITPASSWORD=******

* clonato repository git

		cd /var
		sudo git clone https://github.com/zawinul/eng-docs.git
		sudo chgrp -R developers eng-docs #cambio gruppo a tutto il contenuto


* installato vs code come da istruzioni su https://code.visualstudio.com/docs/setup/linux (alla voce Debian and Ubuntu based distributions)





