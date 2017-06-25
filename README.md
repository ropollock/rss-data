# rss-data
Implements a datastore abstraction that allows you to index, search, and delete from a datastore of RSS articles.
Currently supports ElasticSearch 5.4.x.
Articles can be queried from an intuitive search query builder.

## ArticleDatastore

```java
ArticleDatastore articleDb =
                ArticleDatastoreFactory.getArticleDatastore(
                        ArticleDatastoreFactory.DATASTORE_TYPE.ELASTIC_SEARCH);
```

## Index
```java
ArticleBuilder ab = new ArticleBuilder();
ab.setURL("http://google.com");
ab.setTile("Google search engine");
ab.setContent("A search bar.");
ab.setDescription("The internet.");
ab.setAuthor("Larry Page");
ab.addTag("example", "tag");
articleDb.index(ab.build());
```

## Search term
Perform a multi match query against a list of fields.
```java
ArticleSearchQueryBuilder query = new ArticleSearchQueryBuilder();
query.setSearchQuery("engine");
query.setFields(Article.DEFAULT_SEARCH_FIELDS);
List<ArticleHit> articleHits = articleDb.search(query.build());
```
## Search term and filter
Perform a multi match query against a list of fields and apply post filter.
```java
ArticleSearchQueryBuilder query = new ArticleSearchQueryBuilder();
query.setSearchQuery("engine");
query.setFields(Article.DEFAULT_SEARCH_FIELDS);
query.setAuthor("Larry Page");
List<ArticleHit> articleHits = articleDb.search(query.build());
```

## Search filter
Perform a filter query using a set of post filters defined by SearchQuery.
```java
ArticleSearchQueryBuilder query = new ArticleSearchQueryBuilder();
query.setAuthor("Larry Page");
List<ArticleHit> articleHits = articleDb.search(query.build());
```

## Search for a tag
Perform a filter that matches a specific tag.
```java
ArticleSearchQueryBuilder query = new ArticleSearchQueryBuilder();
query.setTags(new Tag("example", "tag"));
List<ArticleHit> articleHits = articleDb.search(query.build());
```

# Provision
com.russpollock.rss.bootstrap.Provision main provisions an articles index using mappings defined in resources/repository.
The articles index will be dropped and recreated if rss-data.provision.es.dropAndCreate is set to true.

## Files
resources/repository/articles.json
resources/repository/article.json

## Env variables for provisioning
rss-data.provision.es.writeIndex
rss-data.provision.es.dropAndCreate


# Environment variables
## rss-data.es.clusterName 
String representing the clustername for ElasticSearch.
## rss-data.es.transportAddresses
Comma separated list of transport addresses used by the ElasticSearch transport client.
## rss-data.es.articleSearchIndicies
Comma separated list of the default search indicies to use when search queries ElasticSearch.
## rss-data.es.articleWriteIndex
String representing the default write index for index operations to ElasticSearch.
## rss-data.provision.es.writeIndex
String representing the name of the index to create when provisioning ElasticSearch.
## rss-data.provision.es.dropAndCreate
Boolean value used to determine whether or not to drop an existing index when provisioning ElasticSearch.

# Dev setup suggestions
For an elasticsearch instance its recommended to use a local virtual machine with a docker container for elasticsearch.

Official ES docker
https://hub.docker.com/_/elasticsearch/

Another slightly easier to configure container
https://hub.docker.com/r/itzg/elasticsearch/

