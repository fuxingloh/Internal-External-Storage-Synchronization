Internal-External-Storage-Synchronization
=========================================

Moved the project from eclipse adt to Android Studio
Internal storage: sqlite, External storage: cloud like azure, with this library you can sync data. with internet or not

ATM there is no documentation for IES. Request if you want 1.

For SQLite Auto Controller
==========================
Library included in this library:
* guava-16.0.1
* objenesis-2.1
* gson-2.2.2
* mobileservices-1.1.3 (Azure Mobile Service)
* android-support-v4

So how to use?

```java
class Data{
    @com.ravolo.ies.annotations.StorageName("id")//You can use annotations to declare
    private int id;//You need to declare an int id or an exception will be thrown
    private String title;//all this will be saved
    private String description;//saved as well

    @com.ravolo.ies.annotations.StorageBlob()
    private OtherObject dat1;//To store other object you must declare blob
    @com.ravolo.ies.annotations.StorageDoNotInclude()
    private String dat2;//String will be auto stored, declare do not include if you don't want to
}

//Operation
private SqliteAutoController<Data> sqliteController =  new SqliteAutoController<Data>(MyActivity.this, Data.class, 1);//change the version number > (1) if structure of the class change

//Insert
Data data = new Data();
data.description= "Content";
data = sqliteController.insert(data);

//Update
data.description= "Content Updated";
sqliteController.update(data);

//Delete
sqliteController.delete(data);//Delete by object
sqliteController.delete(1);//Delete by int id

//Get the List in db
ArrayList<Data> dataList = new ArrayList<Data>();
dataList.clear();
List<Data> dataListDB = sqliteController.getAll();
dataList.addAll(dataListDB);
//Or you can just do this
ArrayList<Data> dataList = new ArrayList<Data>(sqliteController.getAll());

//LookUp
Data data3 = sqliteController.lookUp(1);//1 is the id

//Custom statement
sqliteController.executeQuery("delete * from Data");
```
