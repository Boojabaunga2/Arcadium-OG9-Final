/* Arcadium shared physical input bridge, v2.
   Android supplies a bitmask. Games can use the virtual Gamepad API and/or keyboard.
   Keyboard events are compatibility only; virtual gamepad provides held-state polling. */
(function(){
  'use strict';
  if(window.__arcadiumBridgeInstalled) return;
  window.__arcadiumBridgeInstalled=true;
  const bits={UP:0,DOWN:1,LEFT:2,RIGHT:3,A:4,B:5,X:6,Y:7,START:8,SELECT:9,L1:10,R1:11};
  const keyMap={UP:['ArrowUp','ArrowUp'],DOWN:['ArrowDown','ArrowDown'],LEFT:['ArrowLeft','ArrowLeft'],RIGHT:['ArrowRight','ArrowRight'],A:[' ','Space'],B:['b','KeyB'],X:['x','KeyX'],Y:['y','KeyY'],START:['Enter','Enter'],SELECT:['Escape','Escape'],L1:['q','KeyQ'],R1:['e','KeyE']};
  const gamepadDriven = /galactic_riot_balanced_v3\.html/i.test(location.pathname);
  let previous=0, current=0;
  const padButtons=Array.from({length:17},()=>({pressed:false,touched:false,value:0}));
  const pad={id:'Arcadium Mini Vibe native bridge',index:0,connected:true,mapping:'standard',timestamp:0,axes:[0,0,0,0],buttons:padButtons};
  const padMap={A:0,B:1,X:2,Y:3,L1:4,R1:5,SELECT:8,START:9,UP:12,DOWN:13,LEFT:14,RIGHT:15};
  function down(name){return !!(current & (1<<bits[name]));}
  function update(){
    try{current=Number(window.ArcadiumNative?.getHeld?.()||0)|0;}catch(e){current=0;}
    for(let name of Object.keys(bits)){
      const pressed=down(name),b=padButtons[padMap[name]];
      if(b){b.pressed=pressed;b.touched=pressed;b.value=pressed?1:0;}
      let changed=!!((current^previous)&(1<<bits[name]));
      if(changed && !gamepadDriven){
        const [key,code]=keyMap[name];
        try{
          const evt=new KeyboardEvent(pressed?'keydown':'keyup',{key,code,bubbles:true,cancelable:true});
          document.dispatchEvent(evt);
        }catch(e){}
      }
    }
    pad.axes[0]=(down('RIGHT')?1:0)-(down('LEFT')?1:0);
    pad.axes[1]=(down('DOWN')?1:0)-(down('UP')?1:0);
    pad.timestamp=performance.now();
    previous=current;
  }
  window.ArcadiumController={pressed:name=>down(name),snapshot:()=>current};
  // Gamepad polling is the authoritative continuous-input path for Galactic Riot.
  try{Object.defineProperty(navigator,'getGamepads',{configurable:true,value:()=>{update();return [pad];}});}catch(e){}
  // Keyboard-centric games receive down/up transitions even without polling gamepads.
  (function pump(){update();requestAnimationFrame(pump);})();
})();
