import { useState, useEffect } from 'react';
import { SOUND_LIBRARY } from '../data/sounds';
import { playSynthesizedSound, stopAllEmissions } from '../utils/audio';

export default function Traps() {
  const [activeTab, setActiveTab] = useState<'TIME_BOMB' | 'PHANTOM'>('TIME_BOMB');
  
  // Time Bomb State
  const [bombTime, setBombTime] = useState(10); // seconds
  const [bombSoundId, setBombSoundId] = useState(SOUND_LIBRARY[0]?.id || '');
  const [isBombArmed, setIsBombArmed] = useState(false);
  const [bombCountdown, setBombCountdown] = useState(0);

  // Phantom State
  const [phantomInterval, setPhantomInterval] = useState(60); // seconds
  const [phantomSoundId, setPhantomSoundId] = useState('RANDOM');
  const [isPhantomArmed, setIsPhantomArmed] = useState(false);

  // --- Timers ---
  useEffect(() => {
    let timer: NodeJS.Timeout;
    if (isBombArmed && bombCountdown > 0) {
      timer = setTimeout(() => setBombCountdown(prev => prev - 1), 1000);
    } else if (isBombArmed && bombCountdown === 0) {
      // Detonate
      const s = SOUND_LIBRARY.find(s => s.id === bombSoundId);
      if (s) {
        playSynthesizedSound(s.category, s.name, s.durationMs, s.assetPath);
      }
      setIsBombArmed(false);
    }
    return () => clearTimeout(timer);
  }, [isBombArmed, bombCountdown, bombSoundId]);

  useEffect(() => {
    let intv: NodeJS.Timeout;
    if (isPhantomArmed) {
      intv = setInterval(() => {
        let s;
        if (phantomSoundId === 'RANDOM') {
          s = SOUND_LIBRARY[Math.floor(Math.random() * SOUND_LIBRARY.length)];
        } else {
          s = SOUND_LIBRARY.find(snd => snd.id === phantomSoundId);
        }
        if (s) {
          playSynthesizedSound(s.category, s.name, s.durationMs, s.assetPath);
        }
      }, phantomInterval * 1000);
    }
    return () => clearInterval(intv);
  }, [isPhantomArmed, phantomInterval, phantomSoundId]);

  const handleArmBomb = () => {
    if (isBombArmed) {
      setIsBombArmed(false);
    } else {
      setBombCountdown(bombTime);
      setIsBombArmed(true);
    }
  };

  const handleTogglePhantom = () => {
    setIsPhantomArmed(!isPhantomArmed);
  };

  // Format countdown
  const formatTime = (secs: number) => {
    const m = Math.floor(secs / 60).toString().padStart(2, '0');
    const s = (secs % 60).toString().padStart(2, '0');
    return `${m}:${s}`;
  };

  return (
    <main className="pt-24 pb-32 px-4 lg:px-8 min-h-screen flex flex-col gap-6 max-w-4xl mx-auto">
      <section className="shrink-0 mb-4">
        <h1 className="text-4xl md:text-5xl font-display-lg font-black uppercase text-transparent bg-clip-text bg-gradient-to-r from-red-500 via-orange-500 to-yellow-500 drop-shadow-[0_0_15px_rgba(239,68,68,0.4)] italic">
          TRAP DEPLOYMENT
        </h1>
        <p className="font-mono text-zinc-400 tracking-widest uppercase mt-2 font-bold italic">Arm time-delayed and repetitive payloads.</p>
      </section>

      {/* Tabs */}
      <div className="flex bg-zinc-900 border-2 border-zinc-700 p-1 rounded-2xl shrink-0">
        <button 
          onClick={() => setActiveTab('TIME_BOMB')}
          className={`flex-1 py-3 rounded-xl font-label-caps text-sm tracking-widest font-black italic transition-all ${
            activeTab === 'TIME_BOMB' 
              ? 'bg-red-500 text-black shadow-[0_0_15px_rgba(239,68,68,0.5)]' 
              : 'text-zinc-500 hover:text-zinc-300'
          }`}
        >
          Time Bomb
        </button>
        <button 
          onClick={() => setActiveTab('PHANTOM')}
          className={`flex-1 py-3 rounded-xl font-label-caps text-sm tracking-widest font-black italic transition-all ${
            activeTab === 'PHANTOM' 
              ? 'bg-fuchsia-500 text-black shadow-[0_0_15px_rgba(217,70,239,0.5)]' 
              : 'text-zinc-500 hover:text-zinc-300'
          }`}
        >
          Phantom Noise
        </button>
      </div>

      {/* Tab Content: TIME BOMB */}
      {activeTab === 'TIME_BOMB' && (
        <section className="flex-1 flex flex-col items-center justify-center bg-zinc-900 border-2 border-red-500/30 rounded-3xl p-6 relative overflow-hidden group">
          <div className="absolute inset-0 bg-red-500/5 opacity-0 group-hover:opacity-100 transition-opacity"></div>
          
          <div className={`relative z-10 w-64 h-64 rounded-full border-4 flex items-center justify-center shadow-2xl mb-8 transition-colors ${isBombArmed ? 'border-red-500 bg-red-500/10 shadow-[0_0_50px_rgba(239,68,68,0.4)] animate-pulse' : 'border-zinc-700 bg-black/40'}`}>
             <span className={`font-mono text-6xl font-black italic tracking-tighter ${isBombArmed ? 'text-red-500 drop-shadow-[0_0_10px_rgba(239,68,68,0.8)]' : 'text-zinc-400'}`}>
               {formatTime(isBombArmed ? bombCountdown : bombTime)}
             </span>
          </div>

          <div className="w-full max-w-sm space-y-6 relative z-10">
            <div>
              <label className="text-[10px] font-mono text-red-400 tracking-widest uppercase font-bold italic mb-2 block">Trigger Delay (Secs)</label>
              <input 
                 type="range" 
                 min="1" max="120" 
                 value={bombTime} 
                 onChange={e => setBombTime(Number(e.target.value))}
                 disabled={isBombArmed}
                 className="w-full accent-red-500 bg-zinc-800 h-2 rounded-full appearance-none outline-none disabled:opacity-50"
              />
            </div>

            <div>
              <label className="text-[10px] font-mono text-red-400 tracking-widest uppercase font-bold italic mb-2 block">Payload Delivery</label>
              <select 
                value={bombSoundId}
                onChange={e => setBombSoundId(e.target.value)}
                disabled={isBombArmed}
                className="w-full bg-black border-2 border-red-500/20 text-red-100 p-4 rounded-xl font-mono text-sm tracking-widest italic outline-none focus:border-red-500 disabled:opacity-50"
              >
                {SOUND_LIBRARY.map(s => (
                  <option key={s.id} value={s.id}>{s.name} ({s.category})</option>
                ))}
              </select>
            </div>

            <button 
              onClick={handleArmBomb}
              className={`w-full py-5 rounded-2xl font-black uppercase italic tracking-[0.2em] transition-all flex justify-center items-center gap-3 ${
                isBombArmed 
                  ? 'bg-zinc-800 text-red-500 border-2 border-red-500 shadow-[0_0_20px_rgba(239,68,68,0.2)]'
                  : 'bg-red-500 text-black border-none shadow-[0_0_30px_rgba(239,68,68,0.6)] hover:bg-red-400 scale-100 hover:scale-[1.02]'
              }`}
            >
              <span className="material-symbols-outlined text-2xl">
                {isBombArmed ? 'cancel' : 'bolt'}
              </span>
              {isBombArmed ? 'DISARM BOMB' : 'ARM BOMB'}
            </button>
          </div>
        </section>
      )}

      {/* Tab Content: PHANTOM */}
      {activeTab === 'PHANTOM' && (
        <section className="flex-1 flex flex-col items-center justify-center bg-zinc-900 border-2 border-fuchsia-500/30 rounded-3xl p-6 relative overflow-hidden group">
          <div className="absolute inset-0 bg-fuchsia-500/5 opacity-0 group-hover:opacity-100 transition-opacity"></div>
          
          <div className="relative z-10 w-full max-w-sm space-y-8">
            <div className="text-center mb-8">
              <span className={`material-symbols-outlined text-8xl transition-colors ${isPhantomArmed ? 'text-fuchsia-400 drop-shadow-[0_0_20px_rgba(217,70,239,0.5)] animate-pulse' : 'text-zinc-700'}`}>
                hearing
              </span>
            </div>

            <div>
              <label className="text-[10px] font-mono text-fuchsia-400 tracking-widest uppercase font-bold italic mb-2 flex justify-between">
                <span>Intermittent Delay</span>
                <span>{phantomInterval} Secs</span>
              </label>
              <input 
                 type="range" 
                 min="5" max="300" step="5"
                 value={phantomInterval} 
                 onChange={e => setPhantomInterval(Number(e.target.value))}
                 disabled={isPhantomArmed}
                 className="w-full accent-fuchsia-500 bg-zinc-800 h-2 rounded-full appearance-none outline-none disabled:opacity-50"
              />
            </div>

            <div>
              <label className="text-[10px] font-mono text-fuchsia-400 tracking-widest uppercase font-bold italic mb-2 block">Payload Delivery</label>
              <select 
                value={phantomSoundId}
                onChange={e => setPhantomSoundId(e.target.value)}
                disabled={isPhantomArmed}
                className="w-full bg-black border-2 border-fuchsia-500/20 text-fuchsia-100 p-4 rounded-xl font-mono text-sm tracking-widest italic outline-none focus:border-fuchsia-500 disabled:opacity-50"
              >
                <option value="RANDOM">🎲 RANDOM SOUND</option>
                {SOUND_LIBRARY.map(s => (
                  <option key={s.id} value={s.id}>{s.name} ({s.category})</option>
                ))}
              </select>
            </div>

            <button 
              onClick={handleTogglePhantom}
              className={`w-full py-5 rounded-2xl font-black uppercase italic tracking-[0.2em] transition-all flex justify-center items-center gap-3 space-y-2 ${
                isPhantomArmed 
                  ? 'bg-zinc-800 text-fuchsia-500 border-2 border-fuchsia-500 shadow-[0_0_20px_rgba(217,70,239,0.2)]'
                  : 'bg-fuchsia-500 text-black border-none shadow-[0_0_30px_rgba(217,70,239,0.6)] hover:bg-fuchsia-400 scale-100 hover:scale-[1.02]'
              }`}
            >
              <span className="material-symbols-outlined text-2xl">
                {isPhantomArmed ? 'power_settings_new' : 'wifi_tethering_error'}
              </span>
              {isPhantomArmed ? 'DEACTIVATE PHANTOM' : 'ACTIVATE PHANTOM'}
            </button>
          </div>
        </section>
      )}

      {/* Emergency Stop Helper */}
      <div className="shrink-0 flex justify-center">
        <button 
          onClick={stopAllEmissions}
          className="bg-black/50 border border-white/10 px-6 py-2 rounded-full text-[10px] font-mono uppercase tracking-widest text-zinc-400 hover:text-white hover:border-white/30 transition-colors italic"
        >
          Emergency Stop All Audio
        </button>
      </div>
    </main>
  );
}
