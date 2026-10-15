import React, { useState } from 'react';
import { Crosshair, Droplet, Hospital } from 'lucide-react';
import { BloodGroup, CreateBloodRequestDto, UrgencyLevel } from '@/types';
import { requestService } from '@/services/requestService';
import { Modal } from '@/components/Modal';
import { ALL_BLOOD_GROUPS } from '@/utils/bloodGroups';
import { useGeolocation } from '@/hooks/useGeolocation';

interface CreateRequestModalProps {
  isOpen: boolean;
  onClose: () => void;
  onCreated: () => void;
}

export const CreateRequestModal: React.FC<CreateRequestModalProps> = ({
  isOpen,
  onClose,
  onCreated,
}) => {
  const { latitude, longitude, loading: geoLoading, error: geoError, getLocation } = useGeolocation();

  const [bloodGroup, setBloodGroup] = useState<BloodGroup>('O+');
  const [unitsRequested, setUnitsRequested] = useState<number>(2);
  const [hospitalName, setHospitalName] = useState('');
  const [hospitalLocation, setHospitalLocation] = useState('');
  const [urgency, setUrgency] = useState<UrgencyLevel>('MEDIUM');
  const [requiredDate, setRequiredDate] = useState('');
  const [notes, setNotes] = useState('');
  const [latInput, setLatInput] = useState('');
  const [lonInput, setLonInput] = useState('');

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  React.useEffect(() => {
    if (latitude !== null && longitude !== null) {
      setLatInput(latitude.toString());
      setLonInput(longitude.toString());
    }
  }, [latitude, longitude]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!hospitalName.trim() || !hospitalLocation.trim()) {
      setError('Please provide hospital name and location details');
      return;
    }

    if (unitsRequested < 1 || unitsRequested > 20) {
      setError('Units requested must be between 1 and 20 units');
      return;
    }

    setError(null);
    setLoading(true);

    try {
      const payload: CreateBloodRequestDto = {
        bloodGroup,
        unitsRequested: Number(unitsRequested),
        hospitalName: hospitalName.trim(),
        hospitalLocation: hospitalLocation.trim(),
        urgency,
        requiredDate: requiredDate || null,
        notes: notes.trim() || undefined,
        latitude: latInput ? parseFloat(latInput) : null,
        longitude: lonInput ? parseFloat(lonInput) : null,
      };

      await requestService.createRequest(payload);
      onCreated();
      onClose();
    } catch (err: any) {
      setError(err?.message || 'Failed to submit blood request');
    } finally {
      setLoading(false);
    }
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose} title="Submit New Blood Request" maxWidth="580px">
      <form onSubmit={handleSubmit}>
        {error && <div className="alert alert-danger">{error}</div>}

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
          <div className="form-group">
            <label className="label" htmlFor="reqBloodGroup">
              Required Blood Group
            </label>
            <select
              id="reqBloodGroup"
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
            <label className="label" htmlFor="units">
              Units Requested (1–20)
            </label>
            <input
              id="units"
              type="number"
              min={1}
              max={20}
              className="input"
              value={unitsRequested}
              onChange={(e) => setUnitsRequested(parseInt(e.target.value, 10))}
              required
            />
          </div>
        </div>

        <div className="form-group">
          <label className="label" htmlFor="hospitalName">
            Hospital or Medical Center Name
          </label>
          <input
            id="hospitalName"
            type="text"
            className="input"
            placeholder="e.g. City General Hospital, Blood Bank Wing"
            value={hospitalName}
            onChange={(e) => setHospitalName(e.target.value)}
            required
          />
        </div>

        <div className="form-group">
          <label className="label" htmlFor="hospitalLocation">
            Hospital Address / Ward Details
          </label>
          <input
            id="hospitalLocation"
            type="text"
            className="input"
            placeholder="e.g. 742 Evergreen Terrace, Ward 3B, ICU"
            value={hospitalLocation}
            onChange={(e) => setHospitalLocation(e.target.value)}
            required
          />
        </div>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
          <div className="form-group">
            <label className="label" htmlFor="urgency">
              Urgency Level
            </label>
            <select
              id="urgency"
              className="select"
              value={urgency}
              onChange={(e) => setUrgency(e.target.value as UrgencyLevel)}
            >
              <option value="LOW">Low (Scheduled elective procedure)</option>
              <option value="MEDIUM">Medium (Required within 24–48 hours)</option>
              <option value="HIGH">High (Urgent surgical need)</option>
              <option value="CRITICAL">Critical (Immediate trauma / emergency)</option>
            </select>
          </div>

          <div className="form-group">
            <label className="label" htmlFor="requiredDate">
              Required By Date (Optional)
            </label>
            <input
              id="requiredDate"
              type="date"
              className="input"
              value={requiredDate}
              onChange={(e) => setRequiredDate(e.target.value)}
            />
          </div>
        </div>

        {/* Coordinates */}
        <div className="form-group">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.375rem' }}>
            <label className="label" style={{ margin: 0 }}>
              Hospital Coordinates (Optional)
            </label>
            <button
              type="button"
              onClick={getLocation}
              disabled={geoLoading}
              className="btn btn-outline btn-sm"
              style={{ fontSize: '0.7rem' }}
            >
              <Crosshair size={12} />
              <span>{geoLoading ? 'Acquiring...' : 'Use My GPS'}</span>
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
          <span className="form-hint">Enables precise Haversine distance ranking for nearby donors.</span>
        </div>

        <div className="form-group">
          <label className="label" htmlFor="notes">
            Clinical Notes / Patient Context
          </label>
          <textarea
            id="notes"
            className="textarea"
            placeholder="e.g. Pre-operative preparation for emergency surgery, patient contact desk..."
            value={notes}
            onChange={(e) => setNotes(e.target.value)}
            maxLength={500}
          />
        </div>

        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem', marginTop: '1.5rem' }}>
          <button type="button" className="btn btn-secondary" onClick={onClose}>
            Cancel
          </button>
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {loading ? 'Creating Request...' : 'Publish Request'}
          </button>
        </div>
      </form>
    </Modal>
  );
};
