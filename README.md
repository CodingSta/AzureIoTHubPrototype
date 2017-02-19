# AzureIoTHubPrototype
// From MinGyu, Ju

// MainActivity.java 파일에서...

// Windows 용 DeviceExplore 등을 사용하여 IoT Hub 에 디바이스를 등록한 후 생성되는 "Connection String" 을 아래의 connString 에 대입한다.

String connString = "Put your IoT device's connection string here!";

// MQTT 프로토콜 대신 HTTP 사용도 가능하다. - Azure Java API 참조.
IotHubClientProtocol protocol = IotHubClientProtocol.MQTT;


// sendData(View v) Method 는 IoT Hub로 랜덤 데이터를 보내기 위한 것이다.

// sendData(View v) 아래에 위치하는 모든 Inner Class 는 Microsoft Azure 에서 제공하는 Java API 를 사용한 것이다.
