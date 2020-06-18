# ContentProvider Query Tester

This is a tool for Android developers to make arbitrary queries on content providers, such as Telephony and Contacts. It is used to pass parameters to *ContentResolver.query()* and see the results quickly.

There are 2 ways of using it.

## 1. Using the app

A very simple app to enter the parameters for *ContentResolver.query()* as literal strings(or comma separated strings for the parameters that require a string array) and see the results in a list.

This is meant to be used as a quick way to check that a query works. For more exhaustive and repeatable testing, it would be better to use an instrumented test instead.


#### Permissions

Currently the app has permissions for READ\_CONTACTS and READ\_SMS set in the manifest. If you need any addition permissions for accessing the content providers that you want, then please add them and rebuild the app:

* Add the permission(s) to the manifest.
* For runtime permissions, add them to the methodRequiresPermissions() method in the MainActivity.

#### Limitations

The app currently does not support doing secondary lookup queries, this can only be done with an instrumented test.

> **What is a secondary lookup?**
> 
> This is when a field is retrieved in a query, and you want to do another query using that field data to get additional or more meaningful information.
> 
> *An example of this is when doing a query on the Telephony content provider to get SMS messages. When you retrieve the phone number for a message you could do a secondary query on the Contacts provider to find the contact name associated with that phone number.*
> 


Please note that, to keep the code straightforward as a development tool, there are no performance enhancements when doing the queries. So when running a query that returns a lot of data (in particular with secondary queries), it might take some time.


## 2. Write an Instrumented Test

A more useful way to use this tool might be to run an instrumented test to do the query. This has several advantages compared to using the app:

* You can use Android constants instead of having to find the actual string literals as parameters, e.g. *Threads.CONTENT_URI* instead of *'content://mms-sms/conversations'*
* You can run the queries with secondary lookup queries. See some of the tests under the *androidTest* directory to find some examples of this.
* The results of the query can be retrieved in various ways. There are utility classes that show the results as individual log statements in LogCat, as JSON in LogCat and as JSON in a saved file to external storage.

#### Permissions

For additional permissions required, as well as adding them to the manifest, also ensure to have the GrantPermissionRule set for those permissions.

## Motivation

Working with Android content providers can sometimes be frustrating, due to poor documentation or inconsistent API.

For instance to get all the columns for a query, some URI will allow passing null for the *projection* parameter to do this. Other URI require passing '\*', and yet for others you will need to explicitly specify all the columns names (null for the *projection* parameter causes error).

Then there may be inconsistencies across different devices. For example on some Samsung devices, using the URI *'content://mms-sms/conversations'* causes an error.

So it makes it easier to test and experiment with the ContentResolver queries in a simple tool before using them in a real app.

## License

    Copyright (C) 2020 David Wong

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
