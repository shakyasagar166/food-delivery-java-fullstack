import React, { useState, useRef, useEffect } from "react";
import "./ChatBot.css";
import axios from "axios";
import { API_URL } from "../../../config/api";
import SmartToyIcon from "@mui/icons-material/SmartToy";
import CloseIcon from "@mui/icons-material/Close";
import SendIcon from "@mui/icons-material/Send";
import AutoAwesomeIcon from "@mui/icons-material/AutoAwesome";

const formatMessage = (text) => {
  if (!text) return "";
  // Simple parser for **bold** text
  const parts = text.split(/(\*\*[^*]+\*\*)/g);
  return parts.map((part, index) => {
    if (part.startsWith("**") && part.endsWith("**")) {
      return <strong key={index}>{part.slice(2, -2)}</strong>;
    }
    return part;
  });
};

const suggestedQueries = [
  "🍕 Pizza kahan milega aur kitne ka hai?",
  "🌱 Veg food options kaunse restaurants me hain?",
  "💰 ₹250 ke andar tasty dishes",
  "🏪 Available restaurants aur unki timing?",
];

const ChatBot = () => {
  const [isOpen, setIsOpen] = useState(false);
  const [messages, setMessages] = useState([
    {
      sender: "bot",
      text: "Namaste! 🙏 Main **QuickBite AI Assistant** hoon (powered by Groq AI).\n\nAap mujhse kisi bhi restaurant, dish, vegetarian options ya exact price ke baare me pooch sakte hain!",
    },
  ]);
  const [input, setInput] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  };

  useEffect(() => {
    if (isOpen) {
      scrollToBottom();
    }
  }, [messages, isOpen]);

  const handleSend = async (messageText) => {
    const textToSend = messageText || input;
    if (!textToSend.trim() || isLoading) return;

    const userMsg = { sender: "user", text: textToSend };
    setMessages((prev) => [...prev, userMsg]);
    setInput("");
    setIsLoading(true);

    try {
      const response = await axios.post(`${API_URL}/api/ai/chat`, {
        message: textToSend,
      });

      const botReply =
        response.data && response.data.reply
          ? response.data.reply
          : "Maaf kijiye, mujhe response generate karne mein pareshani hui. Kripya dobara poochiye!";

      setMessages((prev) => [...prev, { sender: "bot", text: botReply }]);
    } catch (error) {
      console.error("AI Chat Error:", error);
      setMessages((prev) => [
        ...prev,
        {
          sender: "bot",
          text: "Server se connect karne me problem aayi. Kripya thodi der baad check karein!",
        },
      ]);
    } finally {
      setIsLoading(false);
    }
  };

  const handleKeyDown = (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <>
      {/* Floating Action Button */}
      {!isOpen && (
        <button
          className="quickbite-chatbot-fab"
          onClick={() => setIsOpen(true)}
          title="Ask AI Food Guide"
        >
          <span className="pulse-ring"></span>
          <AutoAwesomeIcon sx={{ fontSize: "1.2rem", color: "#ffe082" }} />
          <span>Ask AI Food Guide</span>
        </button>
      )}

      {/* Floating Chat Window */}
      {isOpen && (
        <div className="quickbite-chat-window">
          {/* Header */}
          <div className="chat-header">
            <div className="chat-header-info">
              <div className="chat-header-avatar">
                <SmartToyIcon />
              </div>
              <div>
                <h4 className="chat-header-title">QuickBite AI Guide</h4>
                <p className="chat-header-sub">Dish & Restaurant Assistant • Powered by Groq AI</p>
              </div>
            </div>
            <div className="chat-header-actions">
              <button onClick={() => setIsOpen(false)} title="Close Chat">
                <CloseIcon sx={{ fontSize: "1.2rem" }} />
              </button>
            </div>
          </div>

          {/* Messages Body */}
          <div className="chat-messages">
            {messages.map((msg, idx) => (
              <div key={idx} className={`chat-bubble ${msg.sender}`}>
                {formatMessage(msg.text)}
              </div>
            ))}

            {/* Suggestions on start */}
            {messages.length === 1 && (
              <div className="chat-suggestions">
                {suggestedQueries.map((query, i) => (
                  <button
                    key={i}
                    className="suggestion-chip"
                    onClick={() => handleSend(query)}
                  >
                    {query}
                  </button>
                ))}
              </div>
            )}

            {/* Typing Dots */}
            {isLoading && (
              <div className="chat-typing">
                <span></span>
                <span></span>
                <span></span>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Input Bar */}
          <div className="chat-input-area">
            <input
              type="text"
              placeholder="e.g. Pizza kaunse restaurant me hai aur kitne ka hai..."
              value={input}
              onChange={(e) => setInput(e.target.value)}
              onKeyDown={handleKeyDown}
              disabled={isLoading}
            />
            <button
              onClick={() => handleSend()}
              disabled={isLoading || !input.trim()}
              title="Send Message"
            >
              <SendIcon sx={{ fontSize: "1.1rem" }} />
            </button>
          </div>
        </div>
      )}
    </>
  );
};

export default ChatBot;
