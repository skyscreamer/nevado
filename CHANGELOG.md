Changelog
=========

Version 1.2.5 - 10/24/2013
--------------------------
 - Fix issues with ClassLoader during de/serialization on OSGi
 - ResettableMock interface implemented to improve repeatability on external unit tests

Version 1.2.4 - 8/4/2013
------------------------
 - Fixed big where unfixed suffixes was causing queues ending with "null"
 - Shorter queue names (base32 instead of base16 for random token)
 - Allow setting maximum poll time.  (Defaults to 5000.)

Version 1.2.3 - 7/9/2013
-------------------------
 - Enable suffixes for temporary queues and topics to make them easier to identify
 - Added a method in Connection to simplify deletion of temporary queues and topics

Version 1.2.2 - 7/3/2013
-------------------------
 - Fixed a NullPointerException on some shutdowns

Version 1.2.1 - 6/22/2013
-------------------------
 - Fixed asynchronous support for AWS connector (@shlasouski)
 - Fixed bug with non-transient log object in BytesMessage and StreamMessage
 - Use single thread executor for async AWS calls

Version 1.2.0 - 5/11/2013
-------------------------
 - Add support for asynchronous API calls in AWS connector (thanks @shlasouski!)
 - Make best effort clean-up when exception encountered during connection.close()
 - Modernize the maven dependencies
 - Fixed interrupt handling in Session.stop()
 - Make timing more coarse in BackoffSleeperTest to make it Cloudbees friendly
 - Typica is being **deprecated** going forward due to lack of development

Version 1.1.1 - 1/26/2013
-------------------------
 - Fixed a bug in which Nevado would hang when trying to close the container running it

Version 1.1.0 - 9/3/2012
------------------------
 - Added the ability to override SQS and SNS endpoints through NevadoConnectionFactory
 - Downgraded commons-codec dependency to 1.3 to play nicer with other libraries

Version 1.0.0 - 8/3/2012
------------------------
 - Added support for Amazon's AWS Java SDK

Version 1.0.0-Beta2 - 6/26/2012
-------------------------------
 - Removed runtime spring dependencies
 - Cleaned up test behavior

Version 1.0.0-Beta1 - 5/18/2012
-------------------------------
Initial release!
