//
// typica - A client library for Amazon Web Services
// Copyright (C) 2007 Xerox Corporation
// 
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package com.xerox.amazonws.sqs2;

public enum QueueAttribute {
	ALL ("All"),
	APPROXIMATE_NUMBER_OF_MESSAGES ("ApproximateNumberOfMessages"),
	APPROXIMATE_NUMBER_OF_MESSAGES_NOT_VISIBLE ("ApproximateNumberOfMessagesNotVisible"),
	CREATED_TIMESTAMP ("CreatedTimestamp"),
	LAST_MODIFIED_TIMESTAMP ("LastModifiedTimestamp"),
	VISIBILITY_TIMEOUT ("VisibilityTimeout"),
	REQUEST_PAYER ("RequestPayer"),
	MAXIUMUM_MESSAGE_SIZE ("MaximumMessageSize"),
	MESSAGE_RETENTION_PERIOD ("MessageRetentionPeriod"),
	POLICY ("POLICY"),
    QUEUE_ARN ("QueueArn");

	private final String queryAttribute;

	QueueAttribute(String queryAttr) {
		queryAttribute = queryAttr;
	}

	public String queryAttribute() {
		return queryAttribute;
	}
}
