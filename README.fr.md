![](https://dev.lutece.paris.fr/jenkins/buildStatus/icon?job=cms-plugin-document-deploy)
[![Alerte](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-document&metric=alert_status)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-document)
[![Line of code](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-document&metric=ncloc)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-document)
[![Coverage](https://dev.lutece.paris.fr/sonar/api/project_badges/measure?project=fr.paris.lutece.plugins%3Aplugin-document&metric=coverage)](https://dev.lutece.paris.fr/sonar/dashboard?id=fr.paris.lutece.plugins%3Aplugin-document)

# Plugin Document

## Introduction

Le plugin Document constitue l'application de gestion de contenu d'un portail Lutece.

Ses principales fonctionnalités sont :


 
* La création dynamique de documents structurés. Un type de document est créé en définissant différents attributs : champs texte simples ou enrichis, dates, objets multimédia, pièces jointes, valeurs à choisir dans une liste, ...
* Le cycle de vie des documents (création, validation, publication, archivage, ...) se déroule à l'intérieur d'espaces de classement organisés à la manière de répertoires. Les opérations sur les documents et les espaces sont soumises à des droits d'accès entièrement paramétrables et intégrées dans la gestion de la sécurité de Lutèce.
* Un moteur de règles permet de créer dynamiquement des opérations de gestion des documents telles que des déplacements d'espaces ou des notifications à des listes de diffusion par courrier électronique.
* Les documents sont également catégorisables sur la base d'un thésaurus et peuvent bénéficier d’un système de métadonnées extensible (Dublin Core ou autre).
* Pour la publication, le plugin fournit également deux portlets permettant de publier un ou une liste de documents. Il gère des dates de mise en ligne et de retrait automatique, ainsi qu’un ordonnancement ou une suspension manuel de chacun des documents.
* Les documents sont accessibles par le moteur de recherche. Celui-ci indexe en texte intégral les zones de contenu définies comme « à indexer » dans le type du document.


## Usage

 **Indexation des documents** 

Après la création ou la modification de documents, il est nécessaire de lancer le démon d'indexation `docSearchIndexer` pour que les nouveaux documents ou les modifications soient pris en compte dans les résultats de recherche.

Le démon d'indexation peut être lancé manuellement depuis l'interface d'administration (Système → Démons) ou s'exécute automatiquement selon la configuration définie.

## Configuration

 **Indexation et Parsers** 

Pour une indexation complète des documents, notamment des fichiers binaires (PDF, Office, etc.), il est recommandé d'installer le **plugin-parser** .

Le plugin document fournit un parser HTML par défaut ( `DefaultDocumentIndexerParser` ) qui utilise JSoup pour extraire le texte des contenus HTML. Ce parser a une priorité basse (priorité 2).

Le plugin-parser fournit un parser universel basé sur Apache Tika ( `TikaAutoDetectParser` ) qui a une priorité plus élevée (priorité 1). Ce parser détecte automatiquement le format des documentset peut traiter :
 
* PDF
* Documents MS Office (DOC, XLS, PPT, DOCX, XLSX, PPTX)
* Documents OpenDocument (ODT, ODS, ODP)
* HTML, XML
* Archives (ZIP, RAR, etc.)
* Et de nombreux autres formats


 **Note :** Le plugin-parser apporte des dépendances lourdes (Apache Tika et ses bibliothèques). Si vous n'avez besoin que de l'indexation HTML, le parser par défaut du plugin document suffit.Pour une indexation complète incluant les fichiers binaires, installez le plugin-parser.

Le système de priorité CDI sélectionne automatiquement le parser disponible avec la priorité la plus élevée.

## Modèles

Depuis la version 7.0.0 le plugin n'utilise plus ni XML ni XSL : les documents sont affichés avec des modèles FreeMarker.

 
*  **Types de document** : chaque type de document a un modèle du back office (prévisualisation du contenu de travail) et un modèle du front office (contenu validé), modifiés dans la page du type de document. Un modèle vide signifie quele modèle par défaut du plugin est utilisé ( `admin/plugins/document/document_admin_default.html` , `skin/plugins/document/document_content_default.html` ). Un modèle est vérifié avec un document d'exemple avant d'être enregistré, et un modèle en erreur est remplacé par le modèle par défaut à l'affichage du document.
*  **Modèle de données** : `document` ( `title` , `summary` , `categories` , `attributes` par code d'attribut), `document_type` , et au front office `publication_date` (peut être nul), `site_locale` et `request_parameters` . Un attribut fichier est un objet avec `url` , `fileName` , `contentType` et `image` . Un attribut sans valeur est absent : testez-le avec `??` .
*  **Macros** ( `util/document/macro_document_view.html` , quel que soit le réglage d'échappement du site) : `@documentValue` (texte échappé), `@documentHtml` (texte riche tel quel), `@documentFile` , `@documentFileIcon` et `@documentAttribute` (affichage selon le type de l'attribut).
*  **Rubriques** : les rubriques document et liste de documents utilisent les modèles de rubrique du core (fonctionnalité Gestion des modèles de rubrique). Les modèles livrés dans `skin/plugins/document/portlet/` remplacent les anciens styles XSL ; un site ajoute ses propres modèles dans la fonctionnalité du core.
*  **Métadonnées** : les métadonnées des documents sont stockées sous forme d'objet JSON. Le gestionnaire Dublin Core les affiche en balises meta `DC.*` avec le modèle `skin/plugins/document/document_include_meta.html` .

## Mise à jour vers la version 7.0.0

La version 7.0.0 ne dépend plus du plugin-xmltransformer. Les scripts de mise à jour migrent les données ; les anciennes colonnes sont supprimées lors d'un démarrage ultérieur, quand leur contenu n'est plus nécessaire.

 
*  **Types de document** : les types de document initiaux dont les feuilles de style XSL n'ont jamais été modifiées reçoivent automatiquement les nouveaux modèles. Les types de document ayant des feuilles de style XSL spécifiques sont signalésdans la page des types de document (« XSL à réécrire ») : leurs feuilles de style sont affichées dans la page du type de document, où il faut les réécrire en modèles puis les supprimer. D'ici là, ces types de document utilisent les modèles pardéfaut.
*  **Rubriques** : chaque rubrique utilisant un style XSL livré passe au modèle correspondant. Une rubrique utilisant un style créé par le site utilise le modèle par défaut : choisissez ou créez un autre modèle dans le formulaire de la rubrique.
*  **Métadonnées** : le daemon `legacyMetadataMigration` convertit les métadonnées XML en JSON, puis ne fait plus rien. Un document dont les métadonnées ne peuvent pas être converties est tracé et conservé pour le passage suivant.
*  **Contenus** : les documents sont affichés à partir de leurs attributs. Un document publié dont la seule version validée était son contenu XML doit être validé à nouveau : la requête qui liste ces documents est dans le script de mise à jour `update_db_document-6.0.1-7.0.0.sql` .
*  **API** : les méthodes XML du plugin sont supprimées ( `Document.getXml` , `getXmlWorkingContent` , `getXmlValidatedContent` , `getXmlMetadata` , `AttributeManager.getAttributeXmlValue` , `MetadataHandler.getXmlMetadata` , `DocumentTypeService` , `DocumentAttributesService` , `DocumentSpacesService.getXmlSpacesList` ) : les modules qui les utilisent doivent être mis à jour. Les méthodes dépréciées `getInstance()` des services et des homes de rubriques sont également supprimées : injectez les beans à la place. `Rule.init()` est supprimée (elle n'était jamais appelée). `DocumentResourceServlet` n'est plus un bean CDI : son cache et `putInCache` passent dans le bean `ResourceServletCache` ( `cacheValidatedResource` ). La propriété inutilisée `document.regularExpressionPerPage` est supprimée : le formulaire d'attribut liste toutes ses expressions régulières.
*  **Fichiers** : `servlet/plugins/document/resource` contrôle désormais l'accès aux fichiers. Les fichiers de travail (paramètre `working_content` ) ne sont envoyés qu'aux administrateurs autorisés à voir le document ; les fichiers validés ne sont envoyés aux autres utilisateurs que si le document est valide et publié dans une rubrique publiée dontils peuvent voir la page. Les fichiers des documents non publiés, liés depuis un autre document par exemple, ne sont plus publics. La page inutilisée `jsp/site/plugins/document/DocumentResource.jsp` est supprimée.
*  **Jetons de sécurité du back office** : les actions du back office qui modifient des données (documents, types de document, publication, catégories, règles, espaces) exigent désormais un jeton de sécurité contre les attaques CSRF, etrefusent la requête avec un message « accès refusé » sinon. Un site qui surcharge les templates d'administration du plugin doit ajouter le jeton dans ses copies : `<@input type='hidden' name='token' value='${token}' />` dans les formulaires, et `&token=${token}` dans les liens vers les actions (actions du workflow des documents, déplacement des attributs d'un type de document). La création et la modification des rubriques, qui utilisent le formulaire de rubrique ducore, ne sont pas concernées.


[Maven documentation and reports](https://dev.lutece.paris.fr/plugins/plugin-document/)



 *generated by [xdoc2md](https://github.com/lutece-platform/tools-maven-xdoc2md-plugin) - do not edit directly.*