curl -XPUT 'http://localhost:9200/articles/' -d '
{
  "index" : {
    "number_of_shards" : "3",
    "number_of_replicas" : "1",
    "analysis" : {
      "analyzer" : {
        "content_cleaner" : {
          "filter" : [
            "standard",
            "lowercase",
            "stop",
            "asciifolding"
          ],
          "tokenizer" : "standard"
        }
      }
    }
  }
}
'

curl -XPUT 'http://localhost:9200/articles/_mapping/article' -d '
{
  "article": {
    "properties": {
      "URL": {
        "type": "string"
      },
      "author": {
        "type": "string",
        "analyzer" : "content_cleaner"
      },
      "rawAuthor": {
        "type" : "string", "index" : "not_analyzed"
      },
      "contentType": {
        "type": "string", "index" : "not_analyzed"
      },
      "content" : {
        "type" : "string", "analyzer" : "content_cleaner"
      },
      "rawContent" : {
        "type": "string", "index" : "no"
      },
      "created": {
        "type": "date"
      },
      "description": {
        "type": "string", "analyzer" : "content_cleaner"
      },
      "rawDescription": {
        "type": "string", "index" : "no"
      },
      "links": {
        "properties": {
          "href": {
            "type": "string", "index" : "not_analyzed"
          },
          "rel": {
            "type": "string", "index" : "not_analyzed"
          },
          "type": {
            "type": "string", "index" : "not_analyzed"
          }
        }
      },
      "published": {
        "type": "date"
      },
      "source": {
        "type" : "string",
        "analyzer" : "content_cleaner"
      },
      "rawSource": {
        "type": "string", "index" : "not_analyzed"
      },
      "tags": {
        "type": "nested",
        "include_in_parent" : true,
        "properties": {
          "tag": {
            "type": "string", "index" : "not_analyzed"
          },
          "tagType": {
            "type": "string", "index" : "not_analyzed"
          }
        }
      },
      "title": {
        "type" : "string",
        "analyzer" : "content_cleaner"
      },
      "rawTitle" : {
        "type" : "string",
        "index" : "not_analyzed"
      },
      "updated": {
        "type": "date"
      }
    }
  }
}
'

curl -XPUT 'localhost:9200/articles2/article/1?pretty' -H 'Content-Type: application/json' -d'
{
	"URL": "http://test1.com",
	"author": "Russ pollock",
	"content": "Super dumb content yoooooooooo so dumb",
	"source": "Client esClient = ElasticSearchClient.getInstance().GetClient(); HttpHeaders headers = new HttpHeaders(); headers.setContentType(MediaType.APPLICATION_JSON);",
	"title": "The best thing ever."
}
'
