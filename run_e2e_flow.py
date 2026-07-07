import urllib.request
import urllib.error
import json
import time
import sys

BASE_URL = "http://localhost:8080/api/v1"

def make_request(url, method, headers=None, data=None):
    if headers is None:
        headers = {}
    if data is not None:
        data_bytes = json.dumps(data).encode('utf-8')
        headers['Content-Type'] = 'application/json'
    else:
        data_bytes = None
    
    req = urllib.request.Request(url, data=data_bytes, headers=headers, method=method)
    try:
        with urllib.request.urlopen(req) as res:
            content = res.read().decode('utf-8')
            if not content.strip():
                return res.status, None
            return res.status, json.loads(content)
    except urllib.error.HTTPError as e:
        body = e.read().decode('utf-8')
        try:
            parsed_body = json.loads(body)
            return e.code, parsed_body
        except Exception:
            return e.code, body
    except Exception as e:
        print(f"Network error: {e}")
        sys.exit(1)

def print_step(title):
    print("\n" + "=" * 80)
    print(f" STEP: {title}")
    print("=" * 80)

def main():
    timestamp = int(time.time())
    driver_email = f"driver_{timestamp}@test.com"
    passenger_email = f"passenger_{timestamp}@test.com"
    password = "password123"

    print("Starting End-to-End Ride Lifecycle Automation Test")
    print(f"Driver Email: {driver_email}")
    print(f"Passenger Email: {passenger_email}")

    # 1. Register Driver
    print_step("Registering Driver")
    driver_payload = {
        "email": driver_email,
        "password": password,
        "fullName": "Carlos Mendoza",
        "vehicleType": "Mototaxi",
        "licenseNumber": f"LIC-{timestamp}",
        "soatNumber": f"SOAT-{timestamp}"
    }
    status, res = make_request(f"{BASE_URL}/auth/register/driver", "POST", data=driver_payload)
    if status != 201:
        print(f"Failed to register driver: {res}")
        sys.exit(1)
    
    driver_token = res["accessToken"]
    driver_user_id = res["user"]["id"]
    print(f"Driver registered successfully. User ID: {driver_user_id}")
    driver_headers = {"Authorization": f"Bearer {driver_token}"}

    # 2. Get Driver ID
    print_step("Fetching Driver Aggregate Details")
    status, res = make_request(f"{BASE_URL}/users/{driver_user_id}/driver", "GET", headers=driver_headers)
    if status != 200:
        print(f"Failed to get driver details: {res}")
        sys.exit(1)
    
    driver_id = res["id"]
    print(f"Driver Aggregate ID: {driver_id}")

    # 3. Get Driver's Wallet
    print_step("Fetching Driver's Wallet")
    status, res = make_request(f"{BASE_URL}/monetization/drivers/{driver_id}/wallet", "GET", headers=driver_headers)
    if status != 200:
        print(f"Failed to get driver's wallet: {res}")
        sys.exit(1)
    
    wallet_id = res["id"]
    initial_balance = res["balance"]
    print(f"Wallet ID: {wallet_id}, Initial Balance: S/ {initial_balance}")

    # 4. Recharge Wallet
    print_step("Recharging Wallet (Adding funds)")
    recharge_amount = 30.00
    status, res = make_request(f"{BASE_URL}/monetization/wallets/{wallet_id}/recharge", "POST", headers=driver_headers, data={"amount": recharge_amount})
    if status != 200:
        print(f"Failed to recharge wallet: {res}")
        sys.exit(1)
    
    current_balance = res["wallet"]["balance"]
    print(f"Recharge successful. New Balance: S/ {current_balance}")

    # 5. Toggle Driver Availability to Active
    print_step("Toggling Driver Availability to Available")
    status, res = make_request(f"{BASE_URL}/drivers/{driver_id}/toggle-availability", "POST", headers=driver_headers)
    if status != 200:
        print(f"Failed to toggle availability: {res}")
        sys.exit(1)
    
    print(f"Driver availability toggled. Available: {res['isAvailable']}")

    # 6. Register Passenger
    print_step("Registering Passenger")
    passenger_payload = {
        "email": passenger_email,
        "password": password,
        "fullName": "Juan Perez"
    }
    status, res = make_request(f"{BASE_URL}/auth/register/passenger", "POST", data=passenger_payload)
    if status != 201:
        print(f"Failed to register passenger: {res}")
        sys.exit(1)
    
    passenger_token = res["accessToken"]
    passenger_user_id = res["user"]["id"]
    print(f"Passenger registered successfully. User ID: {passenger_user_id}")
    passenger_headers = {"Authorization": f"Bearer {passenger_token}"}

    # 7. Create Ride Request
    print_step("Creating Ride Request as Passenger")
    ride_fare = 15.00
    request_payload = {
        "origin": "-12.046374, -77.042793",
        "destination": "-12.091384, -77.022812",
        "distanceKm": 8.0,
        "estimatedFare": ride_fare
    }
    status, res = make_request(f"{BASE_URL}/rides/requests", "POST", headers=passenger_headers, data=request_payload)
    if status != 201:
        print(f"Failed to create ride request: {res}")
        sys.exit(1)
    
    request_id = res["id"]
    print(f"Ride request created successfully. Request ID: {request_id}")

    # 8. Get Open Ride Requests as Driver
    print_step("Checking Open Ride Requests as Driver")
    status, res = make_request(f"{BASE_URL}/rides/requests", "GET", headers=driver_headers)
    if status != 200:
        print(f"Failed to get open requests: {res}")
        sys.exit(1)
    
    open_requests = res.get("data", [])
    found = any(r["id"] == request_id for r in open_requests)
    print(f"Total open requests seen by driver: {len(open_requests)}. Our request found: {found}")

    # 9. Apply as Candidate Driver
    print_step("Applying to Ride Request as Driver Candidate")
    status, res = make_request(f"{BASE_URL}/rides/requests/{request_id}/candidates", "POST", headers=driver_headers, data={})
    if status != 201:
        print(f"Failed to apply as candidate: {res}")
        sys.exit(1)
    
    print("Successfully applied as candidate.")

    # 10. Fetch Candidates as Passenger
    print_step("Fetching Driver Candidates as Passenger")
    status, res = make_request(f"{BASE_URL}/rides/requests/{request_id}/candidates", "GET", headers=passenger_headers)
    if status != 200:
        print(f"Failed to get candidates: {res}")
        sys.exit(1)
    
    candidates = res.get("data", [])
    print(f"Found {len(candidates)} candidates.")
    driver_candidate = None
    for c in candidates:
        if c["driverName"] == "Carlos Mendoza":
            driver_candidate = c
            break
    
    if not driver_candidate:
        print("Our driver was not found in the candidate list.")
        sys.exit(1)
        
    candidate_id = driver_candidate["id"]
    print(f"Candidate ID for our driver: {candidate_id}")

    # 11. Select Candidate (Create confirmed Ride)
    print_step("Selecting Driver Candidate as Passenger")
    select_payload = {
        "candidateId": candidate_id
    }
    status, res = make_request(f"{BASE_URL}/rides/requests/{request_id}/select", "POST", headers=passenger_headers, data=select_payload)
    if status != 201:
        print(f"Failed to select candidate: {res}")
        sys.exit(1)
    
    ride_id = res["ride"]["id"]
    ride_status = res["ride"]["status"]
    print(f"Ride confirmed successfully. Ride ID: {ride_id}. Current Status: {ride_status}")

    # 12. Advance status to DRIVER_ON_THE_WAY
    print_step("Advancing Ride: DRIVER_ON_THE_WAY")
    status, res = make_request(f"{BASE_URL}/rides/{ride_id}/advance", "POST", headers=driver_headers, data={"status": "DRIVER_ON_THE_WAY"})
    if status != 200:
        print(f"Failed to advance to DRIVER_ON_THE_WAY: {res}")
        sys.exit(1)
    print(f"Status advanced. Current: {res['status']}")

    # 13. Advance status to DRIVER_ARRIVED
    print_step("Advancing Ride: DRIVER_ARRIVED")
    status, res = make_request(f"{BASE_URL}/rides/{ride_id}/advance", "POST", headers=driver_headers, data={"status": "DRIVER_ARRIVED"})
    if status != 200:
        print(f"Failed to advance to DRIVER_ARRIVED: {res}")
        sys.exit(1)
    print(f"Status advanced. Current: {res['status']}")

    # 14. Advance status to STARTED
    print_step("Advancing Ride: STARTED (Ride in progress)")
    status, res = make_request(f"{BASE_URL}/rides/{ride_id}/advance", "POST", headers=driver_headers, data={"status": "STARTED"})
    if status != 200:
        print(f"Failed to advance to STARTED: {res}")
        sys.exit(1)
    print(f"Status advanced. Current: {res['status']}")

    # 15. Advance status to COMPLETED (Ride finished, commission should be applied)
    print_step("Advancing Ride: COMPLETED (Finishing ride)")
    status, res = make_request(f"{BASE_URL}/rides/{ride_id}/advance", "POST", headers=driver_headers, data={"status": "COMPLETED"})
    if status != 200:
        print(f"Failed to advance to COMPLETED: {res}")
        sys.exit(1)
    print(f"Status advanced. Current: {res['status']}")

    # 16. Verify final wallet balance (should have commission deducted)
    print_step("Verifying Final Wallet Balance & Commission Deduction")
    status, res = make_request(f"{BASE_URL}/monetization/drivers/{driver_id}/wallet", "GET", headers=driver_headers)
    if status != 200:
        print(f"Failed to get final wallet details: {res}")
        sys.exit(1)
    
    final_balance = float(res["balance"])
    expected_commission = ride_fare * 0.05
    expected_balance = recharge_amount - expected_commission
    print(f"Final Wallet Balance: S/ {final_balance:.2f}")
    print(f"Expected Balance: S/ {expected_balance:.2f} (Recharge: S/ {recharge_amount:.2f} - Commission 5% of S/ {ride_fare:.2f}: S/ {expected_commission:.2f})")
    
    if abs(final_balance - expected_balance) < 0.01:
        print("\nWallet balance verification: SUCCESS")
    else:
        print("\nWARNING: Balance mismatch. Please check execution logs.")
        sys.exit(1)

    # 16.5. Scenario 2: Validation Errors (Invalid scores and missing comments)
    print_step("Scenario 2: Validation Errors (Invalid scores and missing comments)")
    
    # Try invalid score for driver (e.g. 6)
    invalid_driver_payload = {"score": 6}
    status, res = make_request(f"{BASE_URL}/trips/{ride_id}/rate-driver", "POST", headers=passenger_headers, data=invalid_driver_payload)
    print(f"Submitting driver score of 6. Expected failure. Status: {status}, Response: {res}")
    if status == 200:
        print("FAIL: Expected validation error for score 6, but got HTTP 200")
        sys.exit(1)
        
    # Try invalid low score for passenger without comment (e.g. score=2, comment=None)
    invalid_passenger_payload = {"score": 2}
    status, res = make_request(f"{BASE_URL}/trips/{ride_id}/rate-passenger", "POST", headers=driver_headers, data=invalid_passenger_payload)
    print(f"Submitting passenger score of 2 without comment. Expected failure. Status: {status}, Response: {res}")
    if status == 200:
        print("FAIL: Expected validation error for score 2 without comment, but got HTTP 200")
        sys.exit(1)

    # 17. Submit Driver Rating (Passenger rates driver)
    print_step("Submitting Driver Rating as Passenger")
    rate_driver_payload = {
        "score": 5
    }
    status, res = make_request(f"{BASE_URL}/trips/{ride_id}/rate-driver", "POST", headers=passenger_headers, data=rate_driver_payload)
    if status != 200:
        print(f"Failed to rate driver: {res}")
        sys.exit(1)
    print(f"Driver rated successfully. Status: {res['driverRatingStatus']}, Score: {res['driverScore']}")

    # 18. Submit Passenger Rating (Driver rates passenger)
    print_step("Submitting Passenger Rating as Driver")
    rate_passenger_payload = {
        "score": 4,
        "comment": "Buen pasajero, muy puntual"
    }
    status, res = make_request(f"{BASE_URL}/trips/{ride_id}/rate-passenger", "POST", headers=driver_headers, data=rate_passenger_payload)
    if status != 200:
        print(f"Failed to rate passenger: {res}")
        sys.exit(1)
    print(f"Passenger rated successfully. Status: {res['passengerRatingStatus']}, Score: {res['passengerScore']}")

    # 19. Check reputations
    print_step("Verifying Driver and Passenger Reputation Read Models")
    status, res = make_request(f"{BASE_URL}/drivers/{driver_id}/reputation", "GET", headers=driver_headers)
    if status != 200:
        print(f"Failed to get driver reputation: {res}")
        sys.exit(1)
    print(f"Driver Reputation: Avg Score = {res['averageScore']}, Total Ratings = {res['totalRatings']}")

    status, res = make_request(f"{BASE_URL}/passengers/{passenger_user_id}/reputation", "GET", headers=passenger_headers)
    if status != 200:
        print(f"Failed to get passenger reputation: {res}")
        sys.exit(1)
    print(f"Passenger Reputation: Avg Score = {res['averageScore']}, Total Ratings = {res['totalRatings']}")

    # 20. Check Driver Aggregate Reputation in Driver Management
    print_step("Verifying Driver Aggregate Reputation updated via event")
    status, res = make_request(f"{BASE_URL}/drivers/{driver_id}", "GET", headers=driver_headers)
    if status != 200:
        print(f"Failed to get driver aggregate details: {res}")
        sys.exit(1)
    print(f"Driver Aggregate details: ratingAverage = {res['ratingAverage']}, ratingCount = {res['ratingCount']}")
    if res['ratingAverage'] != 5.0 or res['ratingCount'] != 1:
        print("WARNING: Driver aggregate reputation was not updated correctly via integration event.")
        sys.exit(1)


    # 22. Scenario 3: Skip Ratings (creating a second ride)
    print_step("Scenario 3: Skip Ratings (creating a second ride)")
    
    # Create another Ride Request
    request_payload_2 = {
        "origin": "-12.046374, -77.042793",
        "destination": "-12.091384, -77.022812",
        "distanceKm": 5.0,
        "estimatedFare": 10.00
    }
    status, res = make_request(f"{BASE_URL}/rides/requests", "POST", headers=passenger_headers, data=request_payload_2)
    if status != 201:
        print(f"Failed to create second ride request: {res}")
        sys.exit(1)
    request_id_2 = res["id"]
    print(f"Second ride request created. Request ID: {request_id_2}")
    
    # Apply as candidate driver
    status, res = make_request(f"{BASE_URL}/rides/requests/{request_id_2}/candidates", "POST", headers=driver_headers, data={})
    if status != 201:
        print(f"Failed to apply to second request: {res}")
        sys.exit(1)
        
    # Fetch candidates
    status, res = make_request(f"{BASE_URL}/rides/requests/{request_id_2}/candidates", "GET", headers=passenger_headers)
    candidates_2 = res.get("data", [])
    candidate_id_2 = next(c["id"] for c in candidates_2 if c["driverName"] == "Carlos Mendoza")
    
    # Select candidate (create second confirmed Ride)
    status, res = make_request(f"{BASE_URL}/rides/requests/{request_id_2}/select", "POST", headers=passenger_headers, data={"candidateId": candidate_id_2})
    ride_id_2 = res["ride"]["id"]
    print(f"Second ride confirmed. Ride ID: {ride_id_2}")
    
    # Advance status to COMPLETED
    for s in ["DRIVER_ON_THE_WAY", "DRIVER_ARRIVED", "STARTED", "COMPLETED"]:
        make_request(f"{BASE_URL}/rides/{ride_id_2}/advance", "POST", headers=driver_headers, data={"status": s})
        
    print("Second ride completed. Now skipping ratings...")
    
    # Skip driver rating by passenger
    status, res = make_request(f"{BASE_URL}/trips/{ride_id_2}/skip-driver-rating", "POST", headers=passenger_headers)
    if status != 200:
        print(f"Failed to skip driver rating: {res}")
        sys.exit(1)
    print(f"Driver rating skipped. Status: {res['driverRatingStatus']}")
    
    # Skip passenger rating by driver
    status, res = make_request(f"{BASE_URL}/trips/{ride_id_2}/skip-passenger-rating", "POST", headers=driver_headers)
    if status != 200:
        print(f"Failed to skip passenger rating: {res}")
        sys.exit(1)
    print(f"Passenger rating skipped. Status: {res['passengerRatingStatus']}")
    
    # Check that driver aggregate reputation remains unchanged (still 1 rating with average 5.0)
    status, res = make_request(f"{BASE_URL}/drivers/{driver_id}", "GET", headers=driver_headers)
    print(f"Driver Aggregate details after skipping: ratingAverage = {res['ratingAverage']}, ratingCount = {res['ratingCount']}")
    if res['ratingAverage'] != 5.0 or res['ratingCount'] != 1:
        print("WARNING: Skipping ratings incorrectly affected driver reputation averages.")
        sys.exit(1)
        
    # 23. Scenario 4: Ride Dispatch Invariants (BR1 - BR13)
    print_step("Scenario 4: Ride Dispatch Invariants and Business Rules Validation")
    
    timestamp_aux = timestamp + 1000
    passenger_aux_email = f"pass_aux_{timestamp_aux}@test.com"
    driver_aux_email = f"driver_aux_{timestamp_aux}@test.com"
    driver_aux_2_email = f"driver_aux2_{timestamp_aux}@test.com"
    
    # Register Aux Passenger
    status, res = make_request(f"{BASE_URL}/auth/register/passenger", "POST", data={
        "email": passenger_aux_email, "password": password, "fullName": "Pasajero Auxiliar"
    })
    pass_aux_token = res["accessToken"]
    pass_aux_headers = {"Authorization": f"Bearer {pass_aux_token}"}
    
    # Register Aux Driver 1
    status, res = make_request(f"{BASE_URL}/auth/register/driver", "POST", data={
        "email": driver_aux_email, "password": password, "fullName": "Chofer Auxiliar 1",
        "vehicleType": "Mototaxi", "licenseNumber": f"LIC-AUX1-{timestamp_aux}", "soatNumber": f"SOAT-AUX1-{timestamp_aux}"
    })
    drv_aux_token = res["accessToken"]
    drv_aux_user_id = res["user"]["id"]
    drv_aux_headers = {"Authorization": f"Bearer {drv_aux_token}"}
    
    # Get Aux Driver 1 ID & Activate
    status, res = make_request(f"{BASE_URL}/users/{drv_aux_user_id}/driver", "GET", headers=drv_aux_headers)
    drv_aux_id = res["id"]
    # Recharge and Activate
    status, res = make_request(f"{BASE_URL}/monetization/drivers/{drv_aux_id}/wallet", "GET", headers=drv_aux_headers)
    wallet_aux_id = res["id"]
    make_request(f"{BASE_URL}/monetization/wallets/{wallet_aux_id}/recharge", "POST", headers=drv_aux_headers, data={"amount": 30.00})
    make_request(f"{BASE_URL}/drivers/{drv_aux_id}/toggle-availability", "POST", headers=drv_aux_headers)
    
    # Register Aux Driver 2 (Start inactive/no available)
    status, res = make_request(f"{BASE_URL}/auth/register/driver", "POST", data={
        "email": driver_aux_2_email, "password": password, "fullName": "Chofer Auxiliar 2 (Inactivo)",
        "vehicleType": "Mototaxi", "licenseNumber": f"LIC-AUX2-{timestamp_aux}", "soatNumber": f"SOAT-AUX2-{timestamp_aux}"
    })
    drv_aux2_token = res["accessToken"]
    drv_aux2_user_id = res["user"]["id"]
    drv_aux2_headers = {"Authorization": f"Bearer {drv_aux2_token}"}
    status, res = make_request(f"{BASE_URL}/users/{drv_aux2_user_id}/driver", "GET", headers=drv_aux2_headers)
    drv_aux2_id = res["id"]
    
    # 23.1. Test BR1: Passenger cannot have another OPEN request
    print("Testing BR1: Create first open request...")
    request_payload_aux = {
        "origin": "-12.046374, -77.042793", "destination": "-12.091384, -77.022812",
        "distanceKm": 5.0, "estimatedFare": 10.00
    }
    status, res = make_request(f"{BASE_URL}/rides/requests", "POST", headers=pass_aux_headers, data=request_payload_aux)
    req_aux_id = res["id"]
    print(f"First request created. ID: {req_aux_id}")
    
    print("Trying to create second open request as same passenger (should fail)...")
    status, res = make_request(f"{BASE_URL}/rides/requests", "POST", headers=pass_aux_headers, data=request_payload_aux)
    print(f"Status: {status}, Response: {res}")
    if status == 201:
        print("FAIL: Created second open request for same passenger (violated BR1)")
        sys.exit(1)
        
    # 23.2. Test BR13: Inactive driver tries to apply (should fail)
    print("Testing BR13: Inactive driver tries to apply...")
    status, res = make_request(f"{BASE_URL}/rides/requests/{req_aux_id}/candidates", "POST", headers=drv_aux2_headers)
    print(f"Status: {status}, Response: {res}")
    if status == 201:
        print("FAIL: Inactive driver successfully applied (violated BR13)")
        sys.exit(1)
        
    # 23.3. Test BR2: Driver cannot apply twice
    print("Testing BR2: Active driver applies first time (should succeed)...")
    status, res = make_request(f"{BASE_URL}/rides/requests/{req_aux_id}/candidates", "POST", headers=drv_aux_headers)
    if status != 201:
        print(f"Failed to apply first time: {res}")
        sys.exit(1)
    cand_aux_id = res["id"]
    print(f"Applied successfully. Candidate ID: {cand_aux_id}")
    
    print("Active driver tries to apply second time (should fail)...")
    status, res = make_request(f"{BASE_URL}/rides/requests/{req_aux_id}/candidates", "POST", headers=drv_aux_headers)
    print(f"Status: {status}, Response: {res}")
    if status == 201:
        print("FAIL: Driver applied twice to same request (violated BR2)")
        sys.exit(1)
        
    # 23.4. Test BR3: Only owner passenger can select candidate
    print("Testing BR3: Passenger main tries to select candidate on passenger aux request (should fail)...")
    status, res = make_request(f"{BASE_URL}/rides/requests/{req_aux_id}/select", "POST", headers=passenger_headers, data={"candidateId": cand_aux_id})
    print(f"Status: {status}, Response: {res}")
    if status == 201:
        print("FAIL: Unauthorized passenger selected candidate (violated BR3)")
        sys.exit(1)
        
    # 23.5. Test BR12: Transition must be sequential (cannot jump from ACCEPTED to STARTED)
    print("Selecting candidate as rightful passenger (creating confirmed Ride)...")
    status, res = make_request(f"{BASE_URL}/rides/requests/{req_aux_id}/select", "POST", headers=pass_aux_headers, data={"candidateId": cand_aux_id})
    ride_aux_id = res["ride"]["id"]
    print(f"Confirmed ride created. ID: {ride_aux_id}")
    
    print("Testing BR12: Driver tries to advance directly to STARTED (should fail)...")
    status, res = make_request(f"{BASE_URL}/rides/{ride_aux_id}/advance", "POST", headers=drv_aux_headers, data={"status": "STARTED"})
    print(f"Status: {status}, Response: {res}")
    if status == 200:
        print("FAIL: Jumped to STARTED status without intermediate steps (violated BR12)")
        sys.exit(1)
        
    # 23.6. Test BR5: Only assigned driver can advance ride status
    print("Testing BR5: Driver main tries to advance passenger aux ride (should fail)...")
    status, res = make_request(f"{BASE_URL}/rides/{ride_aux_id}/advance", "POST", headers=driver_headers, data={"status": "DRIVER_ON_THE_WAY"})
    print(f"Status: {status}, Response: {res}")
    if status == 200:
        print("FAIL: Unassigned driver successfully advanced ride status (violated BR5)")
        sys.exit(1)
        
    # 23.7. Test BR6: Cannot cancel after STARTED
    print("Advancing ride auxiliary sequentially to STARTED...")
    for s in ["DRIVER_ON_THE_WAY", "DRIVER_ARRIVED", "STARTED"]:
        status, res = make_request(f"{BASE_URL}/rides/{ride_aux_id}/advance", "POST", headers=drv_aux_headers, data={"status": s})
        if status != 200:
            print(f"Failed to advance to {s}: {res}")
            sys.exit(1)
            
    print("Testing BR6: Trying to cancel ride after it has STARTED (should fail)...")
    status, res = make_request(f"{BASE_URL}/rides/{ride_aux_id}/cancel", "POST", headers=pass_aux_headers)
    print(f"Status: {status}, Response: {res}")
    if status == 200:
        print("FAIL: Canceled ride that was already STARTED (violated BR6)")
        sys.exit(1)
        
    # Complete ride aux to clean up
    print("Completing auxiliary ride to release driver...")
    make_request(f"{BASE_URL}/rides/{ride_aux_id}/advance", "POST", headers=drv_aux_headers, data={"status": "COMPLETED"})
    
    print("\nSUCCESS: All scenarios (Standard flow, rating validations, skipping, and Ride Dispatch business invariants) executed and verified perfectly!")

if __name__ == "__main__":
    main()
