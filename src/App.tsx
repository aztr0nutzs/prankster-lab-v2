import { useState, useEffect } from 'react';
import Home from './screens/Home';
import Library from './screens/Library';
import Traps from './screens/Traps';
import PrankMessages from './screens/PrankMessages';
import System from './screens/System';
import Navigation from './components/Navigation';
import TopBar from './components/TopBar';
import BootScreen from './components/BootScreen';
import SettingsDrawer from './components/SettingsDrawer';

import SoundForge from './screens/SoundForge';

export default function App() {
  const [currentScreen, setCurrentScreen] = useState('CORE');
  const [showBootScreen, setShowBootScreen] = useState(true);
  const [isSettingsOpen, setIsSettingsOpen] = useState(false);

  if (showBootScreen) {
    return <BootScreen onComplete={() => setShowBootScreen(false)} />;
  }

  return (
    <div className="bg-black text-on-surface font-body-sm min-h-screen selection:bg-primary-container selection:text-on-primary-fixed overflow-x-hidden dark relative">
      {/* Global HUD Overlays */}
      <div className="hud-scanlines fixed inset-0"></div>
      <div className="hud-vignette fixed inset-0 pointer-events-none"></div>
      
      <TopBar screen={currentScreen} onSettingsClick={() => setIsSettingsOpen(true)} />
      
      {currentScreen === 'CORE' && <Home onNavigate={setCurrentScreen} />}
      {currentScreen === 'LIBRARY' && <Library />}
      {currentScreen === 'TRAPS' && <Traps />}
      {currentScreen === 'MESSAGES' && <PrankMessages />}
      {currentScreen === 'SOUND_FORGE' && <SoundForge />}
      {currentScreen === 'SYSTEM' && <System />}
      
      {/* Visualizer Background Element */}
      <div className="fixed inset-0 pointer-events-none waveform-bg opacity-10 -z-10"></div>
      
      <Navigation currentScreen={currentScreen} setCurrentScreen={setCurrentScreen} />
      
      <SettingsDrawer isOpen={isSettingsOpen} onClose={() => setIsSettingsOpen(false)} />
    </div>
  );
}
