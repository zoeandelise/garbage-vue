import json
import pymongo
import sys
from datetime import datetime
from bson import ObjectId

def transform_data(doc):
    """
    转换数据格式，处理_id和日期字段
    """
    # 处理_id字段
    if '_id' in doc and isinstance(doc['_id'], dict) and '$oid' in doc['_id']:
        try:
            doc['_id'] = ObjectId(doc['_id']['$oid'])
        except:
            del doc['_id']
    
    # 处理日期字段
    date_fields = ['create_time', 'update_time']
    for field in date_fields:
        if field in doc and isinstance(doc[field], str):
            try:
                # 解析ISO 8601格式日期并转换为MongoDB Date类型
                doc[field] = datetime.fromisoformat(doc[field].replace('Z', '+00:00'))
            except ValueError:
                # 如果转换失败，可以删除或保留为字符串
                pass
    
    return doc

def import_data_to_mongodb(json_file, mongodb_uri, db_name, collection_name):
    try:
        client = pymongo.MongoClient(mongodb_uri)
        db = client[db_name]
        collection = db[collection_name]
        
        with open(json_file, 'r', encoding='utf-8') as f:
            data = []
            for line in f:
                if line.strip():
                    doc = json.loads(line)
                    doc = transform_data(doc)
                    data.append(doc)
        
        collection.delete_many({})
        
        if data:
            result = collection.insert_many(data)
            print(f"成功导入{len(result.inserted_ids)}条数据到{db_name}.{collection_name}")
        else:
            print("没有数据可导入")
        
    except pymongo.errors.ConnectionFailure:
        print("连接MongoDB失败，请检查连接URI")
        sys.exit(1)
    except pymongo.errors.BulkWriteError as e:
        print(f"批量导入时发生错误: {e.details}")
        sys.exit(1)
    except Exception as e:
        print(f"导入数据时出错: {str(e)}")
        sys.exit(1)

if __name__ == "__main__":
    mongodb_uri = "mongodb://localhost:27017/"
    db_name = "garbage_classification"
    collection_name = "garbage_guide"
    json_file = "garbage_guide_mongodb.json"
    
    import_data_to_mongodb(json_file, mongodb_uri, db_name, collection_name)