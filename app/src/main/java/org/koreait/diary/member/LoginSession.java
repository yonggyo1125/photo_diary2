package org.koreait.diary.member;

public class LoginSession {
    private static Member member;

    public static Member getMember(String memId) {
        if (memId != null && member == null) {
        // 서버에서 가져오는 부분

        }

        return member;
    }

    public static Member getMember() {
        return member;
    }

    public static void setMember(Member member) {
        LoginSession.member = member;
    }

    public static void updateMember(String userId) {
        Member member = getMember(userId);
        LoginSession.member = member;
    }

    /**
     * 로그인 여부 체크
     *
     * @return
     */
    public static boolean isLogin() {
        return member != null;
    }
}
