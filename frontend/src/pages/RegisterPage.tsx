import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import {
  AlertCircle,
  Crosshair,
  Droplet,
  Heart,
  Hospital,
  Lock,
  Mail,
  MapPin,
  Phone,
  User,
  UserPlus,
} from 'lucide-react';
import { useAuth } from '@/hooks/useAuth';
import { BloodGroup, Role } from '@/types';
import { ALL_BLOOD_GROUPS } from '@/utils/bloodGroups';
import { useGeolocation } from '@/hooks/useGeolocation';

export const RegisterPage: React.FC = () => {
  const { register } = useAuth();
  const navigate = useNavigate();
  const { latitude, longitude, loading: geoLoading, error: geoError, getLocation } = useGeolocation();

  const [role, setRole] = useState<Role>('DONOR');
  const [fullName, setFullName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [bloodGroup, setBloodGroup] = useState<BloodGroup>('O+');
  const [phone, setPhone] = useState('');
  const [address, setAddress] = useState('');
  const [latInput, setLatInput] = useState<string>('');
  const [lonInput, setLonInput] = useState<string>('');

  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);

  const handleUseLocation = () => {
    getLocation();
  };

  // Sync GPS to inputs when acquired
  React.useEffect(() => {
    if (latitude !== null && longitude !== null) {
      setLatInput(latitude.toString());
      setLonInput(longitude.toString());
    }
  }, [latitude, longitude]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError(null);

    if (password.length < 6) {
      setError('Password must be at least 6 characters');
      return;
    }

    if (role === 'DONOR' && (!phone.trim() || !address.trim())) {
      setError('Phone number and general address are required for donor registration');
      return;
    }

    setLoading(true);

    try {
      await register({
        fullName: fullName.trim(),
        email: email.trim(),
        password,
        role,
        bloodGroup: role === 'DONOR' ? bloodGroup : undefined,
        phone: role === 'DONOR' ? phone.trim() : undefined,
        address: role === 'DONOR' ? address.trim() : undefined,
        latitude: latInput ? parseFloat(latInput) : null,
        longitude: lonInput ? parseFloat(lonInput) : null,
      });

      navigate(role === 'DONOR' ? '/donor' : '/recipient', { replace: true });
    } catch (err: any) {
      setError(err?.message || 'Registration failed. Please check your details.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="container" style={{ padding: '3.5rem 1.5rem', display: 'flex', justifyContent: 'center' }}>
      <div style={{ width: '100%', maxWidth: '520px' }}>
        <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
          <div
            style={{
              width: '44px',
              height: '44px',
              borderRadius: '10px',
              backgroundColor: 'var(--primary)',
              color: '#fff',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center',
              margin: '0 auto 0.75rem',
            }}
          >
            <Droplet size={24} fill="#fff" />
          </div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 700 }}>Join Blood Matcher</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
            Register as an available donor or a healthcare recipient coordinator
          </p>
        </div>

        <div className="card">
          <div className="card-body">
            {error && (
              <div className="alert alert-danger" style={{ marginBottom: '1.25rem' }}>
                <AlertCircle size={18} style={{ flexShrink: 0 }} />
                <span>{error}</span>
              </div>
            )}

            <form onSubmit={handleSubmit}>
              {/* Role Selection */}
              <div className="form-group">
                <label className="label">I want to register as a:</label>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
                  <button
                    type="button"
                    onClick={() => setRole('DONOR')}
                    className={`btn ${role === 'DONOR' ? 'btn-primary' : 'btn-outline'}`}
                    style={{ justifyContent: 'center', padding: '0.75rem' }}
                  >
                    <Heart size={16} />
                    <span>Blood Donor</span>
                  </button>
                  <button
                    type="button"
                    onClick={() => setRole('RECIPIENT')}
                    className={`btn ${role === 'RECIPIENT' ? 'btn-primary' : 'btn-outline'}`}
                    style={{ justifyContent: 'center', padding: '0.75rem' }}
                  >
                    <Hospital size={16} />
                    <span>Recipient</span>
                  </button>
                </div>
              </div>

              {/* Full Name */}
              <div className="form-group">
                <label className="label" htmlFor="fullName">
                  Full Name
                </label>
                <div style={{ position: 'relative' }}>
                  <input
                    id="fullName"
                    type="text"
                    className="input"
                    placeholder="e.g. Jane Doe"
                    value={fullName}
                    onChange={(e) => setFullName(e.target.value)}
                    required
                    style={{ paddingLeft: '2.25rem' }}
                  />
                  <User
                    size={16}
                    color="var(--text-muted)"
                    style={{ position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)' }}
                  />
                </div>
              </div>

              {/* Email */}
              <div className="form-group">
                <label className="label" htmlFor="email">
                  Email Address
                </label>
                <div style={{ position: 'relative' }}>
                  <input
                    id="email"
                    type="email"
                    className="input"
                    placeholder="name@example.com"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    style={{ paddingLeft: '2.25rem' }}
                  />
                  <Mail
                    size={16}
                    color="var(--text-muted)"
                    style={{ position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)' }}
                  />
                </div>
              </div>

              {/* Password */}
              <div className="form-group">
                <label className="label" htmlFor="password">
                  Password (min. 6 characters)
                </label>
                <div style={{ position: 'relative' }}>
                  <input
                    id="password"
                    type="password"
                    className="input"
                    placeholder="Choose a strong password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    minLength={6}
                    style={{ paddingLeft: '2.25rem' }}
                  />
                  <Lock
                    size={16}
                    color="var(--text-muted)"
                    style={{ position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)' }}
                  />
                </div>
              </div>

              {/* Donor Specific Profile Fields */}
              {role === 'DONOR' && (
                <div
                  style={{
                    backgroundColor: 'var(--bg-surface-alt)',
                    padding: '1.25rem',
                    borderRadius: 'var(--radius-md)',
                    border: '1px solid var(--border-light)',
                    marginBottom: '1.25rem',
                  }}
                >
                  <h3 style={{ fontSize: '0.925rem', fontWeight: 600, marginBottom: '0.875rem', color: 'var(--primary)' }}>
                    Donor Profile Information
                  </h3>

                  <div className="form-group">
                    <label className="label" htmlFor="bloodGroup">
                      ABO/Rh Blood Group
                    </label>
                    <select
                      id="bloodGroup"
                      className="select"
                      value={bloodGroup}
                      onChange={(e) => setBloodGroup(e.target.value as BloodGroup)}
                      required
                    >
                      {ALL_BLOOD_GROUPS.map((bg) => (
                        <option key={bg} value={bg}>
                          {bg}
                        </option>
                      ))}
                    </select>
                  </div>

                  <div className="form-group">
                    <label className="label" htmlFor="phone">
                      Contact Phone Number
                    </label>
                    <div style={{ position: 'relative' }}>
                      <input
                        id="phone"
                        type="tel"
                        className="input"
                        placeholder="e.g. +1 555-0199"
                        value={phone}
                        onChange={(e) => setPhone(e.target.value)}
                        required
                        style={{ paddingLeft: '2.25rem' }}
                      />
                      <Phone
                        size={16}
                        color="var(--text-muted)"
                        style={{ position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)' }}
                      />
                    </div>
                    <span className="form-hint">Concealed until you explicitly accept an invitation.</span>
                  </div>

                  <div className="form-group">
                    <label className="label" htmlFor="address">
                      General Address / City
                    </label>
                    <div style={{ position: 'relative' }}>
                      <input
                        id="address"
                        type="text"
                        className="input"
                        placeholder="e.g. Springfield, Downtown"
                        value={address}
                        onChange={(e) => setAddress(e.target.value)}
                        required
                        style={{ paddingLeft: '2.25rem' }}
                      />
                      <MapPin
                        size={16}
                        color="var(--text-muted)"
                        style={{ position: 'absolute', left: '0.75rem', top: '50%', transform: 'translateY(-50%)' }}
                      />
                    </div>
                  </div>

                  {/* Optional Coordinates with GPS Button */}
                  <div className="form-group" style={{ marginBottom: 0 }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.375rem' }}>
                      <label className="label" style={{ margin: 0 }}>
                        Geographic Coordinates (Optional)
                      </label>
                      <button
                        type="button"
                        onClick={handleUseLocation}
                        disabled={geoLoading}
                        className="btn btn-outline btn-sm"
                        style={{ fontSize: '0.7rem', padding: '0.2rem 0.5rem' }}
                      >
                        <Crosshair size={12} />
                        <span>{geoLoading ? 'Detecting...' : 'Use My GPS'}</span>
                      </button>
                    </div>

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.5rem' }}>
                      <input
                        type="number"
                        step="any"
                        className="input"
                        placeholder="Latitude"
                        value={latInput}
                        onChange={(e) => setLatInput(e.target.value)}
                      />
                      <input
                        type="number"
                        step="any"
                        className="input"
                        placeholder="Longitude"
                        value={lonInput}
                        onChange={(e) => setLonInput(e.target.value)}
                      />
                    </div>
                    {geoError && <span className="form-error">{geoError}</span>}
                    <span className="form-hint">Used for Haversine distance calculations when matching.</span>
                  </div>
                </div>
              )}

              <button type="submit" className="btn btn-primary btn-full" disabled={loading}>
                <UserPlus size={16} />
                <span>{loading ? 'Creating Account...' : 'Complete Registration'}</span>
              </button>
            </form>
          </div>

          <div
            className="card-footer"
            style={{ justifyContent: 'center', fontSize: '0.8125rem', color: 'var(--text-secondary)' }}
          >
            Already registered?{' '}
            <Link to="/login" style={{ fontWeight: 600, marginLeft: '0.25rem' }}>
              Sign In
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};
