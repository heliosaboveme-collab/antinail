/**
 * main.js (所持金管理・連戦・賭け金変更対応版)
 */
let deck, player, dealer;
let canPeek = true;
let isGameOver = false;

// 🏆 修正：お金のデータ
let balance = 1000; 
let currentBet = 100; // BET_AMOUNT を変数 currentBet に変更

// ゲームの初期化
function initGame() {
    deck = new Deck();
    deck.shuffle(); 
    
    player = new Player("Player");
    dealer = new Player("Dealer");
    canPeek = true;
    isGameOver = false;

    // 初期配布
    player.addCard(deck.draw());
    player.addCard(deck.draw());
    dealer.addCard(deck.draw());
    dealer.addCard(deck.draw());

    // UIのリセット
    document.getElementById("result-message").innerText = "";
    document.getElementById("peek-btn").style.opacity = "1";
    document.getElementById("peek-btn").disabled = false;
    document.getElementById("hit-btn").disabled = false;
    document.getElementById("stand-btn").disabled = false;
    document.getElementById("next-round-btn").style.display = "none";

    // 🏆 追加：プレイ中は賭け金変更ボタンを無効化
    toggleBetButtons(true);

    updateDisplay(false);
    updateMoneyDisplay(); 
}

/**
 * 🏆 追加：賭け金を表示・更新する
 */
function updateMoneyDisplay() {
    document.getElementById("balance").innerText = balance;
    // HTML側の賭け金表示部分
    document.getElementById("bet").innerText = currentBet;
}


/**
 * 🏆 修正：賭け金を変更する関数
 */
function changeBet(amount) {
    // ゲーム中（カードを引いている最中）は変更不可にする
    // 「次のラウンドへ」ボタンが出ていない、かつゲームオーバーでない時（＝待機中）に変更を許可
    if (isGameOver === false) {
        let newBet = currentBet + amount;

        // 【下限チェック】100ドル未満にはならない
        if (newBet < 100) {
            newBet = 100;
        }

        // 【上限チェック】現在の所持金を超えない
        if (newBet > balance) {
            newBet = balance;
        }

        // 値を確定させて表示を更新
        currentBet = newBet;
        updateMoneyDisplay();
    }
}


function toggleBetButtons(disabled) {
    const plusBtn = document.getElementById("bet-plus");
    const minusBtn = document.getElementById("bet-minus");
    if (plusBtn && minusBtn) {
        plusBtn.disabled = disabled;
        minusBtn.disabled = disabled;
    }
}

function hit() {
    if (isGameOver) return;
    player.addCard(deck.draw());
    if (player.calculateScore() > 21) {
        finishRound("バースト！あなたの負けです。");
    } else {
        updateDisplay(false);
    }
}

// スタンドボタン
function stand() {
    if (isGameOver) return;
    while (dealer.calculateScore() < 17) {
        dealer.addCard(deck.draw());
    }
    judge();
}

// 透視スキル
function peek() {
    if (canPeek && !isGameOver) {
        const secretCard = dealer.hand[0];
        alert("【スキル発動】ディーラーの伏せカードは " + secretCard.suit + secretCard.rank + " です！");
        canPeek = false;
        document.getElementById("peek-btn").style.opacity = "0.5";
        document.getElementById("peek-btn").disabled = true;
    }
}

// 勝敗判定
function judge() {
    const pScore = player.calculateScore();
    const dScore = dealer.calculateScore();
    let resultText = "";

    if (dScore > 21) {
        resultText = "ディーラーがバースト！あなたの勝ち！";
    } else if (pScore > dScore) {
        resultText = "あなたの勝ち！";
    } else if (pScore < dScore) {
        resultText = "ディーラーの勝ち...";
    } else {
        resultText = "引き分け！";
    }
    finishRound(resultText);
}

// ラウンド終了処理
function finishRound(message) {
    isGameOver = true;
    updateDisplay(true);
    
    const resultElement = document.getElementById("result-message");
    resultElement.innerText = message;

    // 🏆 修正：賭け金（currentBet）を増減させる
    if (message.includes("ディーラーの勝ち") || message.includes("負け")) {
        balance -= currentBet;
        resultElement.style.color = "#ff4d4d";
    } else if (message.includes("あなたの勝ち") || message.includes("バースト！あなたの勝ち")) {
        balance += currentBet;
        resultElement.style.color = "#f1c40f";
    } else {
        resultElement.style.color = "white";
    }

    updateMoneyDisplay();

    // 操作ボタンを無効化
    document.getElementById("hit-btn").disabled = true;
    document.getElementById("stand-btn").disabled = true;
    document.getElementById("peek-btn").disabled = true;

    // 🏆 追加：ラウンド終了後は賭け金変更ボタンを再度有効にする
    toggleBetButtons(false);

    // 次のラウンドボタンの表示制御
    if (balance <= 0) {
        resultElement.innerText += " 【破産】リタイアしてください。";
        document.getElementById("next-round-btn").style.display = "none";
    } else {
        document.getElementById("next-round-btn").style.display = "inline-block";
        // 🏆 追加：所持金が現在の賭け金より少なくなった場合、自動で調整
        if (currentBet > balance) {
            currentBet = balance;
            updateMoneyDisplay();
        }
    }
}

// 画面更新
function updateDisplay(isFinished) {
    const playerHandDiv = document.getElementById("player-hand");
    const dealerHandDiv = document.getElementById("dealer-hand");

    playerHandDiv.innerHTML = player.hand.map(card => 
        `<img src="${card.getImagePath()}" class="card-img">`
    ).join("");
    document.getElementById("player-score").innerText = player.calculateScore();

    if (isFinished) {
        dealerHandDiv.innerHTML = dealer.hand.map(card => 
            `<img src="${card.getImagePath()}" class="card-img">`
        ).join("");
        document.getElementById("dealer-score").innerText = dealer.calculateScore();
    } else {
        const backCardTag = `<img src="./torannpu-illust53.png" class="card-img">`;
        const visibleCardsTags = dealer.hand.slice(1).map(card => 
            `<img src="${card.getImagePath()}" class="card-img">`
        ).join("");
        
        dealerHandDiv.innerHTML = backCardTag + visibleCardsTags;
        document.getElementById("dealer-score").innerText = "?";
    }
}