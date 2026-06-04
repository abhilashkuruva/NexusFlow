import bcrypt

# Generate BCrypt hashes for user passwords
admin_hash = bcrypt.hashpw(b'admin123', bcrypt.gensalt(rounds=10)).decode()
analyst_hash = bcrypt.hashpw(b'analyst123', bcrypt.gensalt(rounds=10)).decode()
manager_hash = bcrypt.hashpw(b'manager123', bcrypt.gensalt(rounds=10)).decode()

print(f"admin123 hash: {admin_hash}")
print(f"analyst123 hash: {analyst_hash}")
print(f"manager123 hash: {manager_hash}")