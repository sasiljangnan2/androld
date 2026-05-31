# content_test.py
# 에뮬레이터에서 테스트용 통화기록 3개를 삽입하는 스크립트 (Windows PowerShell에서 실행)
# 사용: python .\content_test.py

import subprocess

entries = [
    ("+821027680039", "1620000000000", "10", "1"),  # INCOMING
    ("+821027604438", "1620000100000", "0", "3"),   # MISSED
    ("+821027658001", "1620000200000", "5", "2"),  # OUTGOING
]

for number, date_ms, duration, type_ in entries:
    cmd = (
        f"adb shell content insert --uri content://call_log/calls "
        f"--bind number:s:'{number}' "
        f"--bind date:l:{date_ms} "
        f"--bind duration:l:{duration} "
        f"--bind type:i:{type_}"
    )
    print('Executing:', cmd)
    subprocess.run(cmd, shell=True)

print('Done. 에뮬레이터의 통화기록 앱 또는 본 앱에서 확인하세요.')

