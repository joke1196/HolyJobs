# Introduction
Ce document fait office de rapport pour le projet qui a été réalisé durant le cours de Scala de 3ème année de l'HEIG-VD en filière TIC.  
Nous avons décidé de réaliser un projet de gestion de jobs de vacances, à l'aide d'une application Web implémentant un back-end (Play), un front-end (Scala.js) et une base de données (Slick). Dans cette application, il est possible de rechercher et de postuler pour des jobs, ou même d'en proposer un ! Le site contient 3 pages : la page d'accueil / de recherche, la page de détails d'un job et la page d'ajout d'un job.  
Le travail a été séparé en deux : David s'occupe du back-end et de la base de données, tandis que Miguel s'occupe du front-end tout en travaillant avec certaines actions du back-end.

# Scala.js
## Introduction
Scala.js (https://www.scala-js.org/) est, comme son nom pourrait le présager, une bibliothèque permettant d'écrire du Scala à la place du JavaScript dans la partie front-end d'un site, permettant ainsi d'avoir un langage typé offrant donc plus de sécurité et plus de contrôle, ainsi que d'avoir une approche plus "fonctionnelle" du code.
## Motivations
A la base, nous souhaitions utiliser Scala.js car cette technologie a été suggérée par les responsables du cours, et aussi par curiosité personnelle.  
Nous nous sommes malheureusement rapidement heurté à de nombreux obstacles qui nous on fait perdre un précieux temps. Après discussion avec les responsables, il a été décidé que Scala.js serait retiré du projet et remplacé par du JavaScript, pour des raisons qui seront énoncées plus bas.
## Mise en place
Afin de nous faciliter la vie, nous nous sommes basés sur l'exemple donné dans le cours (https://github.com/vmunier/play-with-scalajs-example), que nous avons adapté pour générer notre projet. Les premiers problèmes sont apparus lorsqu'il a fallu installer et configurer Slick dans ce projet (les explications sont disponibles dans le chapitre réservé à Slick).
## Analyse de la librairie
Après avoir suivi le tutoriel fourni sur le site de la librairie sans trop de mal, nous avons tenté de l'utiliser dans l'application.  
Comme dit plus haut, nous nous sommes très rapidement heurté à un mur : nous souhaitions en effet utiliser la version JQuery de Scala.js pour réaliser quelques animations (scrolling de la page d'accueil, apparition des détails liés à un job lors du survol de la souris sur l'élément, etc.), mais la documentation fournie (https://www.scala-js.org/api/scalajs-jquery/0.8/#org.scalajs.jquery.JQuery) ne contient que les signatures des fonctions, sans autre explication. C'est après quelques heures de combat que nous avons décidé d'abandonner son utilisation au profil de celle de JS.  
La librairie étant en effet actuellement en version Alpha, elle est encore malheureusement trop "nouvelle" et cela se fait ressentir... Elle est tout d'abord très peu documentée (outre la documentation de JQuery, il existe quelques tutoriels et 2-3 exemples basiques par-ci, par-là) et possède une communauté actuellement restreinte. En résumé, elle est donc actuellement utilisable pour des choses dites "basiques" (événements sur des composants du DOM, changements simplistes du DOM, etc.), mais devient très barbante dès que nous souhaitons réaliser des choses plus complexes.

# Conclusions
## Etat des lieux
A l'heure actuelle, le projet n'est pas terminé à 100% (il reste en effet à réaliser les champs de recherche de jobs, ainsi que la pagination), mais nous restons positifs quand à son bon déroulement et quand au suivi de ce qui avait été prévu. Nous arriverons en effet - sauf en cas de tremblement de terre ou autre inconvénients de ce genre - à terminer le projet dans les temps et seront donc prêts pour la présentation.

## Conclusion
Ce projet nous a permi de nous exercer dans l'utilisation des technologies Web utilisant Scala, comme le Framework Play, Scala.js et Slick. Nous sommes très enthousiastes quand à Play qui nous a paru être un bon choix de back-end, ainsi que pour Slick qui offre une nouvelle approche de communication avec la DB, mais sommes au contraires beaucoup moins convaincus par Scala.js...  
Il nous a été difficile de mener à bien ce projet, tant nous étions surchargés en fin de semestres, mais sommes heureux d'y être tout de même arrivés.  
Nous jugeons notre travail comme étant bon, bien que pas parfait.
