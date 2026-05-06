import { useEffect, useRef } from 'react';

interface BootScreenProps {
  onComplete: () => void;
}

export default function BootScreen({ onComplete }: BootScreenProps) {
  const videoRef = useRef<HTMLVideoElement>(null);

  useEffect(() => {
    const video = videoRef.current;
    if (video) {
      video.play().catch((err) => {
        // Autoplay might be blocked, fallback to completion after 4 seconds
        console.warn("Autoplay blocked for boot video:", err);
        setTimeout(onComplete, 4000);
      });
      
      const handleEnded = () => {
        onComplete();
      };
      
      video.addEventListener('ended', handleEnded);
      
      // Failsafe timeout in case event doesn't fire
      const timeout = setTimeout(onComplete, 8000);
      
      return () => {
        video.removeEventListener('ended', handleEnded);
        clearTimeout(timeout);
      };
    } else {
        setTimeout(onComplete, 4000);
    }
  }, [onComplete]);

  return (
    <div className="fixed inset-0 z-[100] bg-black flex items-center justify-center">
        <video 
            ref={videoRef}
            src="/prankster_boot.mp4" 
            className="w-full h-full object-cover"
            muted
            playsInline
        />
        <div className="absolute bottom-10 left-0 right-0 flex justify-center">
            <div className="flex gap-2">
                <div className="w-2 h-2 rounded-full bg-lime-400 animate-[bounce_1s_infinite_0ms]"></div>
                <div className="w-2 h-2 rounded-full bg-orange-500 animate-[bounce_1s_infinite_200ms]"></div>
                <div className="w-2 h-2 rounded-full bg-cyan-400 animate-[bounce_1s_infinite_400ms]"></div>
            </div>
        </div>
    </div>
  );
}
