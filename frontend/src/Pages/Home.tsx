import React from "react";
import styles from "../assets/css/Home.module.css";

export const Home: React.FC = () => {
    return (
        <div className={styles.container}>
            <header className={styles.header}>
                <h1 className={styles.title}>Reddit Vault</h1>
                <p className={styles.subtitle}>
                    Securely archive and access all your saved Reddit posts and comments in
                    one easy-to-use vault.
                </p>
                <a
                    href="http://localhost:8080/auth"
                    target="_blank"
                    className={styles.button}
                    aria-label="Get started with Reddit Vault"
                >
                    Get Started
                </a>
            </header>

            <section className={styles.featuresSection} aria-label="Features">
                <div className={styles.featureBox}>
                    <h2 className={styles.featureTitle}>Automatic Backup</h2>
                    <p className={styles.featureDescription}>
                        Keep your saved posts and comments backed up effortlessly with automated
                        sync.
                    </p>
                </div>
                <div className={styles.featureBox}>
                    <h2 className={styles.featureTitle}>Offline Access</h2>
                    <p className={styles.featureDescription}>
                        View your saved Reddit content anytime, even without internet connection.
                    </p>
                </div>
                <div className={styles.featureBox}>
                    <h2 className={styles.featureTitle}>Privacy Focused</h2>
                    <p className={styles.featureDescription}>
                        Your data is stored securely and never shared with third parties.
                    </p>
                </div>
            </section>

            <footer className={styles.footer}>
                &copy; {new Date().getFullYear()} Reddit Vault. All rights reserved.
            </footer>
        </div>
    );
};
export default Home;