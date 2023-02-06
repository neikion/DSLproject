# DSLproject
android project. this app provide TimeTable, Bus Information, Weather Information, Calendar, Noticeboard, University Building Location Information.

This project was created by me belonging to team easy하조.

this app provide TimeTable, Bus Information, Weather Information, Calendar, Noticeboard, University Building Location Information.

My part is Server Connection, TimeTable, Alarm, Notification, BUS Information and other functions used throughout the project and linked each function into one.

DSLmanager is singleton, main function is Server Connect.

Class named MenuBaseActivity and Menu is menu function.

timetable is separated from the View and works alone.

Services are used for alarm, notification, music player.


Warning

Server connect needs SSL certification file. but not included.

this project don't used public SSL certification file. but I made a way to use a public SSL certification file

music file don't upload because of copyright.




-----------------------------------------------




이 프로젝트는 제가 팀 easy하조에 소속되어 만들었습니다.

이 앱은 시간표, 버스 정보 제공, 날씨 정보 제공, 캘린더, 게시판, 대학 건물 위치 정보 기능을 제공합니다.

제가 맡은 부분은 서버와 https연결, 시간표, 알람, 알림, 버스 정보와 그 외 프로젝트 전반적으로 사용되는 함수와 각 기능들을 하나로 연결하는 것을 맡았습니다.

DSLManager는 프로젝트 전반적으로 사용되는 함수를 모아놓은 싱글톤으로, 주로 네트워크와 관련된 부분을 담당합니다.

MenuBaseActivity 및 Menu라는 이름이 들어간 클래스는 어떤 화면에서든 사용할 수 있고, 클래스만 바꾼다면 기능도 바꿀 수 있도록 합니다.

TimeTable은 화면과는 분리되어 다른 화면에서도 동작 가능하도록 만들었습니다.

프로젝트 내의 Service들은 알람(Alarm)과 헤드업 알림(Notification), 음악 재생을 위해 사용합니다.



주의

서버와 통신하기 위해서는 SSL 인증서 파일이 필요하지만 현제 리포지트리에서는 포함하지 않았습니다.

공인 SSL 인증서를 사용하는 방식을 만들어 놓았으나, 프로젝트 진행시 사설 SSL 인증서로 진행하여 인증서를 개발자가 확인하는 방식을 사용하였습니다.

또한 mp3파일은 저작권 때문에 올리지 않았습니다.
